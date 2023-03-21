package com.adobe.locust.impl;

import com.adobe.locust.api.StatesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

@Controller("/tasks")
public class TasksController {

    /**
     * Sample produced tasks output
     * {
     *   "per_class": {
     *     "QuickstartUser": {
     *       "ratio": 0.0,
     *       "tasks": {
     *         "queryGoliveUrls": {
     *           "ratio": 1.0
     *         }
     *       }
     *     }
     *   },
     *   "total": {
     *     "QuickstartUser": {
     *       "ratio": 0.0,
     *       "tasks": {
     *         "queryGoliveUrls": {
     *           "ratio": 0.0
     *         }
     *       }
     *     }
     *   }
     * }
     */

    @Inject
    private StatesService statesService;

    @Get(produces="application/json")
    public String index() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(statesService.getTasks());
    }
}