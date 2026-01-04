package com.homifybackend.service.salelisting;

import com.homifybackend.dto.ZestimateRequest;
import com.homifybackend.dto.ZestimateResponse;

public interface ZestimateService {
    /**
     * Calculate AI-powered property price estimate (Zestimate)
     * 
     * @param request Property features (47 fields)
     * @return Zestimate response with predicted price, confidence, and range
     */
    ZestimateResponse calculateZestimate(ZestimateRequest request);
}
