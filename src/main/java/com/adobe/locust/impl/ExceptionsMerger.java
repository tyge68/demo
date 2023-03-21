package com.adobe.locust.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.List;

public class ExceptionsMerger {
    private final JsonNode mergedResult;

    public ExceptionsMerger(List<JsonNode> newExceptions) {
        ObjectMapper om = new ObjectMapper();
        mergedResult = om.createObjectNode();
        ArrayNode mergedArray = mergedResult.withArray("exceptions");
        newExceptions.forEach(excNode -> {
            ArrayNode exceptions = excNode.withArray("exceptions");
            exceptions.iterator().forEachRemaining(mergedArray::add);
        });
    }

    public JsonNode merged() {
        return mergedResult;
    }
}
