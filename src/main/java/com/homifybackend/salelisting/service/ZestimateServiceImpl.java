package com.homifybackend.salelisting.service;

import com.homifybackend.salelisting.dto.ZestimateRequest;
import com.homifybackend.salelisting.dto.ZestimateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        try {
            log.info("Starting zestimate calculation for property type: {}", request.getPropertyType());
            
            // Convert request to ML features format (47 features)
            String jsonInput = convertToMLFormat(request);
            log.debug("Request JSON: {}", jsonInput);
            
            // Write JSON to temp file to avoid command line escaping issues
            tempFile = File.createTempFile("zestimate_", ".json");
            objectMapper.writeValue(tempFile, objectMapper.readTree(jsonInput));
            log.debug("Temp file created: {}", tempFile.getAbsolutePath());
            
            // Build Python process with file path
            ProcessBuilder processBuilder = new ProcessBuilder(
                pythonPath,
                scriptPath,
                tempFile.getAbsolutePath()
            );
            
            // Set working directory to project root
            processBuilder.directory(new File(System.getProperty("user.dir")));
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
            // Clean up temp file
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
    
    /**
     * Convert ZestimateRequest to ML model format (47 features with one-hot encoding)
     */
    private String convertToMLFormat(ZestimateRequest req) throws IOException {
        Map<String, Object> features = new HashMap<>();
        
        // Basic Info (6)
        features.put("usableArea", req.getUsableArea() != null ? req.getUsableArea() : 0.0);
        features.put("totalArea", req.getTotalArea() != null ? req.getTotalArea() : 0.0);
        features.put("bedrooms", req.getBedrooms() != null ? req.getBedrooms() : 0);
        features.put("bathrooms", req.getBathrooms() != null ? req.getBathrooms() : 0);
        features.put("floors", req.getFloors() != null ? req.getFloors() : 0);
        features.put("yearBuilt", req.getYearBuilt() != null ? req.getYearBuilt() : 2024);
        
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
        features.put("landArea", req.getLandArea() != null ? req.getLandArea() : 0.0);
        features.put("frontWidth", req.getFrontWidth() != null ? req.getFrontWidth() : 0.0);
        features.put("roadWidth", req.getRoadWidth() != null ? req.getRoadWidth() : 0.0);
        features.put("depth", req.getDepth() != null ? req.getDepth() : 0.0);
        features.put("frontyardArea", req.getFrontyardArea() != null ? req.getFrontyardArea() : 0.0);
        features.put("backyardArea", req.getBackyardArea() != null ? req.getBackyardArea() : 0.0);
        features.put("lotArea", req.getLotArea() != null ? req.getLotArea() : 0.0);
        features.put("gardenArea", req.getGardenArea() != null ? req.getGardenArea() : 0.0);
        features.put("parkingSpaces", req.getParkingSpaces() != null ? req.getParkingSpaces() : 0);
        features.put("floor", req.getFloor() != null ? req.getFloor() : 0);
        
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
        features.put("walkScore", req.getWalkScore() != null ? req.getWalkScore() : 5);
        features.put("bikeScore", req.getBikeScore() != null ? req.getBikeScore() : 5);
        features.put("transitScore", req.getTransitScore() != null ? req.getTransitScore() : 5);
        features.put("quietness", req.getQuietness() != null ? req.getQuietness() : 5);
        features.put("securityLevel", req.getSecurityLevel() != null ? req.getSecurityLevel() : 5);
        
        // Computed (2)
        int totalRooms = (req.getBedrooms() != null ? req.getBedrooms() : 0) + 
                        (req.getBathrooms() != null ? req.getBathrooms() : 0);
        features.put("totalRooms", totalRooms);
        
        int propertyAge = 2026 - (req.getYearBuilt() != null ? req.getYearBuilt() : 2024);
        features.put("propertyAge", propertyAge);
        
        return objectMapper.writeValueAsString(features);
    }
    
    private double boolToDouble(Boolean value) {
        return (value != null && value) ? 1.0 : 0.0;
    }
}