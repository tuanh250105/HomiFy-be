package com.homifybackend.controller.salelisting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.homifybackend.dto.ZestimateRequest;
import com.homifybackend.dto.ZestimateResponse;
import com.homifybackend.service.salelisting.ZestimateService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/zestimate")
public class ZestimateController {
    
    private static final Logger log = LoggerFactory.getLogger(ZestimateController.class);
    
    @Autowired
    private ZestimateService zestimateService;
    
    /**
     * POST /api/zestimate/calculate
     * Calculate AI price prediction for property
     * 
     * @param request Property features (47 fields)
     * @return Zestimate response with price and confidence
     */
    @PostMapping("/calculate")
    public ResponseEntity<ZestimateResponse> calculateZestimate(
            @Valid @RequestBody ZestimateRequest request) {
        
        log.info("Received zestimate calculation request for {} property", 
                request.getPropertyType());
        
        ZestimateResponse response = zestimateService.calculateZestimate(request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/zestimate/health
     * Check if ML service is healthy
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Zestimate service is running");
    }
}
