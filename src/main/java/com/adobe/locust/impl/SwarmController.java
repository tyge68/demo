package com.adobe.locust.impl;

import com.adobe.locust.api.StatesService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller("/swarm")
public class SwarmController {

    @Inject
    private StatesService statesService;

    @Post(consumes = "application/x-www-form-urlencoded", produces="application/json")
    public String main(@Body String form) {
        return statesService.doSwarm(form);
    }
}