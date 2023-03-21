package com.adobe.locust.impl;

import com.adobe.locust.api.StatesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

@Controller("/stats/requests")
public class StatsRequestController {

    /**
     * Sample produced stats/requets output
     * {
     *   "current_response_time_percentile_1": 180,
     *   "current_response_time_percentile_2": 820,
     *   "errors": [
     *     {
     *       "error": "HTTPError(&#x27;405 Client Error: Not allowed. for url: Fetching FH&#x27;)",
     *       "method": "GET",
     *       "name": "Fetching FH",
     *       "occurrences": 14
     *     }
     *   ],
     *   "fail_ratio": 0.5,
     *   "state": "running",
     *   "stats": [
     *     {
     *       "avg_content_length": 223,
     *       "avg_response_time": 674.8145654999332,
     *       "current_fail_per_sec": 1.2857142857142858,
     *       "current_rps": 1.2857142857142858,
     *       "max_response_time": 1231,
     *       "median_response_time": 640,
     *       "method": "GET",
     *       "min_response_time": 177,
     *       "name": "Fetching FH",
     *       "ninetieth_response_time": 820,
     *       "ninety_ninth_response_time": 1200,
     *       "num_failures": 14,
     *       "num_requests": 14,
     *       "safe_name": "Fetching FH"
     *     },
     *     {
     *       "avg_content_length": 0,
     *       "avg_response_time": 0,
     *       "current_fail_per_sec": 0,
     *       "current_rps": 1.2857142857142858,
     *       "max_response_time": 0,
     *       "median_response_time": 0,
     *       "method": "CUSTOM",
     *       "min_response_time": 0,
     *       "name": "X-Cache Fastly MISS",
     *       "ninetieth_response_time": 0,
     *       "ninety_ninth_response_time": 0,
     *       "num_failures": 0,
     *       "num_requests": 14,
     *       "safe_name": "X-Cache Fastly MISS"
     *     },
     *     {
     *       "avg_content_length": 111.5,
     *       "avg_response_time": 337.4072827499666,
     *       "current_fail_per_sec": 1.2857142857142858,
     *       "current_rps": 2.5714285714285716,
     *       "max_response_time": 1231,
     *       "median_response_time": 0,
     *       "method": "",
     *       "min_response_time": 0,
     *       "name": "Aggregated",
     *       "ninetieth_response_time": 790,
     *       "ninety_ninth_response_time": 1200,
     *       "num_failures": 14,
     *       "num_requests": 28,
     *       "safe_name": "Aggregated"
     *     }
     *   ],
     *   "total_rps": 2.5714285714285716,
     *   "user_count": 1
     * }
     */

    @Inject
    private StatesService statesService;

    @Get(produces="application/json")
    public String index() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(statesService.getStatsRequests());
    }
}