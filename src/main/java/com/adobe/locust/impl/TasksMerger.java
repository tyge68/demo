package com.adobe.locust.impl;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class TasksMerger {
    private final JsonNode mergeResult;

    public TasksMerger(List<JsonNode> newTasks) {
        mergeResult = newTasks.get(0);
    }

    public JsonNode merged() {
        return mergeResult;
    }
}
