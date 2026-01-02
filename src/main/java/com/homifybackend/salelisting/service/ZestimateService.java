package com.homifybackend.salelisting.service;

import com.homifybackend.salelisting.dto.ZestimateRequest;
import com.homifybackend.salelisting.dto.ZestimateResponse;

public interface ZestimateService {
    /**
     * Calculate AI-powered property price estimate (Zestimate)
     * 
     * @param request Property features (47 fields)
     * @return Zestimate response with predicted price, confidence, and range
     */
    ZestimateResponse calculateZestimate(ZestimateRequest request);
}
