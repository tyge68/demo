package com.adobe.locust.impl;

import com.adobe.locust.api.StatesService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

@Controller("/stats/reset")
public class ResetController {

    @Inject
    private StatesService statesService;

    @Get(produces="application/json")
    public String main() {
        return statesService.doReset();
    }
}