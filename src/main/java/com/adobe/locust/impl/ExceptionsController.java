package com.adobe.locust.impl;

import com.adobe.locust.api.StatesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

@Controller("/exceptions")
public class ExceptionsController {

    /**
     * Sample produced exceptions output
     * {
     *   "exceptions": []
     * }
     */

    @Inject
    private StatesService statesService;

    @Get(produces="application/json")
    public String index() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(statesService.getExceptions());
    }
}