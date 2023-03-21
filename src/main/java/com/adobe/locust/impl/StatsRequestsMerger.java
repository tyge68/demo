package com.adobe.locust.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class StatsRequestsMerger {
    private static final String TOTAL_RPS = "total_rps";
    private static final String USER_COUNT = "user_count";
    private static final String STATE = "state";
    private static final String STATS = "stats";
    private static final String FAIL_RATIO = "fail_ratio";
    private static final String ERRORS = "errors";
    private static final String WORKERS = "workers";
    private static final String CURRENT_RESPONSE_TIME_PERC_1 = "current_response_time_percentile_1";
    private static final String CURRENT_RESPONSE_TIME_PERC_2 = "current_response_time_percentile_2";
    private final ObjectNode mergedResult;

    public StatsRequestsMerger(List<JsonNode> newStatsRequests) {
        ObjectMapper om = new ObjectMapper();
        mergedResult = om.createObjectNode();
        mergedResult.set(ERRORS, mergedArray(ERRORS, newStatsRequests));
        mergedResult.set(WORKERS, mergedArray(WORKERS, newStatsRequests));
        mergedResult.put(STATE, mergedState(newStatsRequests));
        mergedResult.put(USER_COUNT, mergedInt(USER_COUNT, newStatsRequests).sum());
        mergedResult.put(TOTAL_RPS, mergedDouble(TOTAL_RPS, newStatsRequests).sum());
        mergedResult.put(FAIL_RATIO, asDouble(mergedDouble(FAIL_RATIO, newStatsRequests).average()));
        mergedResult.put(CURRENT_RESPONSE_TIME_PERC_1, asDouble(mergedDouble(CURRENT_RESPONSE_TIME_PERC_1, newStatsRequests).average()));
        mergedResult.put(CURRENT_RESPONSE_TIME_PERC_2, asDouble(mergedDouble(CURRENT_RESPONSE_TIME_PERC_2, newStatsRequests).average()));
        mergedResult.set(STATS, mergedStats(newStatsRequests));
    }
    private double asDouble(OptionalDouble optDouble) {
        return optDouble.isPresent() ? optDouble.getAsDouble(): 0;
    }
    private String mergedState(List<JsonNode> newStatsRequests) {
        for (JsonNode node:newStatsRequests) {
            String state = node.get(STATE).asText();
            if ("running".equalsIgnoreCase(state)) {
                return state;
            }
        }
        return "stopped";
    }
    private JsonNode mergedStats(List<JsonNode> newStatsRequests) {
        ArrayNode mergedResult = new ObjectMapper().createArrayNode();
        HashMap<String, List<JsonNode>> mapStats = new HashMap<>();
        for (JsonNode node:newStatsRequests) {
            ArrayNode stats = node.withArray(STATS);
            stats.forEach(s -> {
                String key = s.get("safe_name").asText();
                List<JsonNode> list = mapStats.getOrDefault(key, new ArrayList<>());
                list.add(s);
                mapStats.put(key, list);
            });
        }
        mapStats.values().forEach(list -> {
            ObjectNode mergedStat = new ObjectMapper().createObjectNode();
            String[] mergeableDoubleFieldSum = new String[] {
                    "current_fail_per_sec",
                    "current_rps"
            };
            String[] mergeableDoubleFieldAvg = new String[] {
                    "avg_content_length",
                    "avg_response_time"
            };
            String[] mergeableDoubleFieldMax = new String[] {
                    "max_response_time",
                    "median_response_time",
                    "ninetieth_response_time",
                    "ninety_ninth_response_time"
            };
            String[] mergeableIntField = new String[] {
                    "num_failures",
                    "num_requests"
            };
            for (String field : mergeableDoubleFieldSum) {
                mergedStat.put(field, mergedDouble(field, list).sum());
            }
            for (String field : mergeableDoubleFieldAvg) {
                mergedStat.put(field, asDouble(mergedDouble(field, list).average()));
            }
            for (String field : mergeableDoubleFieldMax) {
                mergedStat.put(field, asDouble(mergedDouble(field, list).max()));
            }
            for (String field : mergeableIntField) {
                mergedStat.put(field, mergedInt(field, list).sum());
            }
            mergedStat.put("min_response_time", asDouble(mergedDouble("min_response_time", list).min()));
            String[] mergeableStringField = new String[] {
                    "method",
                    "name",
                    "safe_name"
            };
            for (String field : mergeableStringField) {
                mergedStat.put(field, list.get(0).get(field).asText());
            }
            mergedResult.add(mergedStat);
        });
        return mergedResult;
    }
    private DoubleStream mergedDouble(String fieldName, List<JsonNode> newStatsRequests) {
        return newStatsRequests.stream().mapToDouble(excNode -> excNode.get(fieldName).asDouble());
    }
    private IntStream mergedInt(String fieldName, List<JsonNode> newStatsRequests) {
        return newStatsRequests.stream().mapToInt(excNode -> excNode.get(fieldName).asInt());
    }
    private ArrayNode mergedArray(String fieldName, List<JsonNode> newStatsRequests) {
        ArrayNode arr = new ObjectMapper().createArrayNode();
        newStatsRequests.forEach(excNode -> {
            ArrayNode exceptions = excNode.withArray(fieldName);
            exceptions.iterator().forEachRemaining(arr::add);
        });
        return arr;
    }

    public JsonNode merged() {
        return mergedResult;
    }
}
