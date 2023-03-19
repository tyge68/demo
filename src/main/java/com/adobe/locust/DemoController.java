package com.adobe.locust;

import io.micronaut.http.annotation.*;

@Controller("/demo")
public class DemoController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}