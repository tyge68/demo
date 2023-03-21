package com.adobe.locust.api;

import com.fasterxml.jackson.databind.JsonNode;

public interface StatesService {

    JsonNode getTasks();
    JsonNode getExceptions();
    JsonNode getStatsRequests();
    String doSwarm(String form);
    String doStop();
    String doReset();
}
