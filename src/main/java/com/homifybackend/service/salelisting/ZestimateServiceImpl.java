package com.homifybackend.service.salelisting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homifybackend.dto.ZestimateRequest;
import com.homifybackend.dto.ZestimateResponse;

@Service
public class ZestimateServiceImpl implements ZestimateService {
    
    private static final Logger log = LoggerFactory.getLogger(ZestimateServiceImpl.class);
    
    @Value("${zestimate.python.path:python}")
    private String pythonPath;
    
    @Value("${zestimate.script.path:ml/predict.py}")
    private String scriptPath;
    
    @Value("${zestimate.timeout.seconds:30}")
    private long timeoutSeconds;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public ZestimateResponse calculateZestimate(ZestimateRequest request) {
        File tempFile = null;
        File tempDir = null;
        try {
            log.info("Starting zestimate calculation for property type: {}", request.getPropertyType());
            
            // Convert request to ML features format (47 features)
            String jsonInput = convertToMLFormat(request);
            log.debug("Request JSON: {}", jsonInput);
            
            // Write JSON to temp file to avoid command line escaping issues
            tempFile = File.createTempFile("zestimate_", ".json");
            objectMapper.writeValue(tempFile, objectMapper.readTree(jsonInput));
            log.debug("Temp file created: {}", tempFile.getAbsolutePath());
            
            // Extract Python script and model files from classpath to temp folder (for JAR deployment)
            tempDir = extractMLFilesToTempDir();
            File scriptFile = new File(tempDir, "predict.py");
            
            // Build Python process with file path
            ProcessBuilder processBuilder = new ProcessBuilder(
                pythonPath,
                scriptFile.getAbsolutePath(),
                tempFile.getAbsolutePath()
            );
            
            // Set working directory to temp folder (so script can find model.pkl and scaler.pkl)
            processBuilder.directory(tempDir);
            processBuilder.redirectErrorStream(true);
            
            // Start process
            Process process = processBuilder.start();
            
            // Read output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                    log.debug("Python output: {}", line);
                }
            }
            
            // Wait for process to complete with timeout
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroy();
                log.error("Python script timeout after {} seconds", timeoutSeconds);
                throw new RuntimeException("Zestimate calculation timeout");
            }
            
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.error("Python script failed with exit code: {}, output: {}", exitCode, output);
                throw new RuntimeException("Zestimate calculation failed: " + output);
            }
            
            // Parse response
            String jsonOutput = output.toString();
            log.info("Python script completed successfully");
            log.debug("Response JSON: {}", jsonOutput);
            
            ZestimateResponse response = objectMapper.readValue(jsonOutput, ZestimateResponse.class);
            
            log.info("Zestimate calculated: {} with confidence: {}", 
                    response.getZestimate(), response.getConfidence());
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Zestimate calculation interrupted", e);
            throw new RuntimeException("Zestimate calculation interrupted", e);
        } catch (IOException e) {
            log.error("IO error during zestimate calculation", e);
            throw new RuntimeException("Zestimate calculation IO error: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during zestimate calculation", e);
            throw new RuntimeException("Zestimate calculation failed: " + e.getMessage(), e);
        } finally {
            // Clean up temp files
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            if (tempDir != null && tempDir.exists()) {
                deleteDirectory(tempDir);
            }
        }
    }
    
    /**
     * Extract all ML files (script, model, scaler) from classpath to temp directory for JAR deployment
     */
    private File extractMLFilesToTempDir() throws IOException {
        // Create temp directory for ML files
        File tempDir = Files.createTempDirectory("zestimate_ml_").toFile();
        tempDir.deleteOnExit();
        
        // Extract all necessary files
        String[] mlFiles = {"predict.py", "model.pkl", "scaler.pkl"};
        
        for (String filename : mlFiles) {
            ClassPathResource resource = new ClassPathResource("ml/" + filename);
            File targetFile = new File(tempDir, filename);
            
            try (InputStream in = resource.getInputStream();
                 FileOutputStream out = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            
            log.debug("Extracted {} to: {}", filename, targetFile.getAbsolutePath());
        }
        
        // Create ml subdirectory and copy model files there (script expects ml/model.pkl)
        File mlSubdir = new File(tempDir, "ml");
        mlSubdir.mkdir();
        
        Files.copy(
            new File(tempDir, "model.pkl").toPath(),
            new File(mlSubdir, "model.pkl").toPath()
        );
        Files.copy(
            new File(tempDir, "scaler.pkl").toPath(),
            new File(mlSubdir, "scaler.pkl").toPath()
        );
        
        log.debug("Created ml subdirectory with model files at: {}", mlSubdir.getAbsolutePath());
        
        return tempDir;
    }
    
    /**
     * Recursively delete directory
     */
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
    
    /**
     * Convert ZestimateRequest to ML model format (47 features with one-hot encoding)
     */
    private String convertToMLFormat(ZestimateRequest req) throws IOException {
        Map<String, Object> features = new HashMap<>();
        
        // Basic Info (6)
        features.put("usableArea", req.getUsableArea() != null ? req.getUsableArea().doubleValue() : 0.0);
        features.put("totalArea", req.getTotalArea() != null ? req.getTotalArea().doubleValue() : 0.0);
        features.put("bedrooms", req.getBedrooms() != null ? req.getBedrooms().intValue() : 0);
        features.put("bathrooms", req.getBathrooms() != null ? req.getBathrooms().intValue() : 0);
        features.put("floors", req.getFloors() != null ? req.getFloors().intValue() : 0);
        features.put("yearBuilt", req.getYearBuilt() != null ? req.getYearBuilt().intValue() : 2024);
        
        // Property Type One-Hot (4)
        String propertyType = req.getPropertyType() != null ? req.getPropertyType() : "SINGLE_HOUSE";
        features.put("propertyType_SINGLE_HOUSE", propertyType.equals("SINGLE_HOUSE") ? 1.0 : 0.0);
        features.put("propertyType_TOWN_HOUSE", propertyType.equals("TOWN_HOUSE") ? 1.0 : 0.0);
        features.put("propertyType_APARTMENT", propertyType.equals("APARTMENT") ? 1.0 : 0.0);
        features.put("propertyType_VILLA", propertyType.equals("VILLA") ? 1.0 : 0.0);
        
        // Direction One-Hot (8)
        String direction = req.getDirection() != null ? req.getDirection() : "SOUTH";
        features.put("direction_EAST", direction.equals("EAST") ? 1.0 : 0.0);
        features.put("direction_WEST", direction.equals("WEST") ? 1.0 : 0.0);
        features.put("direction_SOUTH", direction.equals("SOUTH") ? 1.0 : 0.0);
        features.put("direction_NORTH", direction.equals("NORTH") ? 1.0 : 0.0);
        features.put("direction_NORTHEAST", direction.equals("NORTHEAST") ? 1.0 : 0.0);
        features.put("direction_NORTHWEST", direction.equals("NORTHWEST") ? 1.0 : 0.0);
        features.put("direction_SOUTHEAST", direction.equals("SOUTHEAST") ? 1.0 : 0.0);
        features.put("direction_SOUTHWEST", direction.equals("SOUTHWEST") ? 1.0 : 0.0);
        
        // Structure (10)
        features.put("landArea", req.getLandArea() != null ? req.getLandArea().doubleValue() : 0.0);
        features.put("frontWidth", req.getFrontWidth() != null ? req.getFrontWidth().doubleValue() : 0.0);
        features.put("roadWidth", req.getRoadWidth() != null ? req.getRoadWidth().doubleValue() : 0.0);
        features.put("depth", req.getDepth() != null ? req.getDepth().doubleValue() : 0.0);
        features.put("frontyardArea", req.getFrontyardArea() != null ? req.getFrontyardArea().doubleValue() : 0.0);
        features.put("backyardArea", req.getBackyardArea() != null ? req.getBackyardArea().doubleValue() : 0.0);
        features.put("lotArea", req.getLotArea() != null ? req.getLotArea().doubleValue() : 0.0);
        features.put("gardenArea", req.getGardenArea() != null ? req.getGardenArea().doubleValue() : 0.0);
        features.put("parkingSpaces", req.getParkingSpaces() != null ? req.getParkingSpaces().intValue() : 0);
        features.put("floor", req.getFloor() != null ? req.getFloor().intValue() : 0);
        
        // Entertainment (4)
        features.put("hasPool", boolToDouble(req.getHasPool()));
        features.put("hasGym", boolToDouble(req.getHasGym()));
        features.put("hasHomeTheater", boolToDouble(req.getHasHomeTheater()));
        features.put("hasGameRoom", boolToDouble(req.getHasGameRoom()));
        
        // Security (4)
        features.put("hasSecurityCamera", boolToDouble(req.getHasSecurityCamera()));
        features.put("has24hSecurity", boolToDouble(req.getHas24hSecurity()));
        features.put("hasSmartLock", boolToDouble(req.getHasSmartLock()));
        features.put("hasSecurityDoor", boolToDouble(req.getHasSecurityDoor()));
        
        // Outdoor (3)
        features.put("hasGarden", boolToDouble(req.getHasGarden()));
        features.put("hasPlayground", boolToDouble(req.getHasPlayground()));
        features.put("hasRooftop", boolToDouble(req.getHasRooftop()));
        
        // Appliances (6)
        features.put("hasDishwasher", boolToDouble(req.getHasDishwasher()));
        features.put("hasDryer", boolToDouble(req.getHasDryer()));
        features.put("hasMicrowave", boolToDouble(req.getHasMicrowave()));
        features.put("hasOven", boolToDouble(req.getHasOven()));
        features.put("hasRefrigerator", boolToDouble(req.getHasRefrigerator()));
        features.put("hasWasher", boolToDouble(req.getHasWasher()));
        
        // Ratings (5)
        features.put("walkScore", req.getWalkScore() != null ? req.getWalkScore().intValue() : 5);
        features.put("bikeScore", req.getBikeScore() != null ? req.getBikeScore().intValue() : 5);
        features.put("transitScore", req.getTransitScore() != null ? req.getTransitScore().intValue() : 5);
        features.put("quietness", req.getQuietness() != null ? req.getQuietness().intValue() : 5);
        features.put("securityLevel", req.getSecurityLevel() != null ? req.getSecurityLevel().intValue() : 5);
        
        // Computed (2)
        int totalRooms = (req.getBedrooms() != null ? req.getBedrooms().intValue() : 0) + 
                        (req.getBathrooms() != null ? req.getBathrooms().intValue() : 0);
        features.put("totalRooms", totalRooms);
        
        int propertyAge = 2026 - (req.getYearBuilt() != null ? req.getYearBuilt().intValue() : 2024);
        features.put("propertyAge", propertyAge);
        
        return objectMapper.writeValueAsString(features);
    }
    
    private double boolToDouble(Boolean value) {
        return (value != null && value) ? 1.0 : 0.0;
    }
}