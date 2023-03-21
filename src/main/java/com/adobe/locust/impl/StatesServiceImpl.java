package com.adobe.locust.impl;

import com.adobe.locust.api.StatesService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Primary;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpStatus;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;

@Primary
@Singleton
public class StatesServiceImpl implements StatesService, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(StatesServiceImpl.class);
    private JsonNode tasks;
    private JsonNode exceptions;
    private JsonNode statsRequests;
    private boolean running = true;

    private final List<String> targets;

    public StatesServiceImpl() {
        ObjectMapper objectMapper = new ObjectMapper();
        tasks = objectMapper.createObjectNode();
        exceptions = objectMapper.createObjectNode();
        statsRequests = objectMapper.createObjectNode();
        targets = sanitizeTargets(Application.targets);
        LOG.info("Targets:{}", String.join(",", targets));
        Thread thread = new Thread(this);
        thread.start();
    }

    private List<String> sanitizeTargets(List<String> targets) {
        return targets.stream().filter(t -> !t.isBlank()).collect(Collectors.toList());
    }

    @Override
    public JsonNode getTasks() {
        return tasks;
    }

    public String doSwarm(String form) {
        String lastResponse = "{}";
        for (String target:targets) {
            try {
                HttpRequest swarmPostRequest = HttpRequest.newBuilder()
                        .uri(new URI(String.format("%s/swarm", target)))
                        .header(CONTENT_TYPE, "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(form))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(swarmPostRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != HttpStatus.OK.getCode()) {
                    LOG.warn("Unexpected response for swarm on {}", target);
                } else {
                    LOG.info("Swarm successful on from {} with {}", target, form);
                    lastResponse = response.body();
                }
            } catch (IOException | InterruptedException | URISyntaxException e) {
                LOG.error("An error occurred trying to proxy swarm request with {}", target);
            }
        }
        return lastResponse;
    }
    public String doStop() {
        String lastResponse = "{}";
        for (String target:targets) {
            try {
                HttpRequest swarmPostRequest = HttpRequest.newBuilder()
                        .uri(new URI(String.format("%s/stop", target)))
                        .GET()
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(swarmPostRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != HttpStatus.OK.getCode()) {
                    LOG.warn("Unexpected response for stop on {}", target);
                } else {
                    lastResponse = response.body();
                }
            } catch (IOException | InterruptedException | URISyntaxException e) {
                LOG.error("An error occurred trying to proxy stop request with {}", target);
            }
        }
        return lastResponse;
    }
    public String doReset() {
        String lastResponse = "{}";
        for (String target:targets) {
            try {
                HttpRequest swarmPostRequest = HttpRequest.newBuilder()
                        .uri(new URI(String.format("%s/stats/reset", target)))
                        .GET()
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(swarmPostRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != HttpStatus.OK.getCode()) {
                    LOG.warn("Unexpected response for reset on {}", target);
                } else {
                    lastResponse = response.body();
                }
            } catch (IOException | InterruptedException | URISyntaxException e) {
                LOG.error("An error occurred trying to proxy reset request with {}", target);
            }
        }
        return lastResponse;
    }

    @Override
    public JsonNode getExceptions() {
        return exceptions;
    }

    @Override
    public JsonNode getStatsRequests() {
        return statsRequests;
    }

    @Override
    public void run() {
        while(running) {
            // do get tasks, exceptions, statsRequests from targeted locust instances.
            try {
                Thread collectTasksThread = new Thread(() -> {
                    List<JsonNode> newTasks = getCollect("tasks");
                    tasks = new TasksMerger(newTasks).merged();
                });
                Thread collectExceptionsThread =new Thread(() -> {
                    List<JsonNode> newExceptions = getCollect("exceptions");
                    exceptions = new ExceptionsMerger(newExceptions).merged();
                });
                Thread collectStatsRequestsThread =new Thread(() -> {
                    List<JsonNode> newStatsRequests = getCollect("stats/requests");
                    statsRequests = new StatsRequestsMerger(newStatsRequests).merged();
                });
                collectTasksThread.start();
                collectExceptionsThread.start();
                collectStatsRequestsThread.start();
                // Wait all jobs completed
                collectTasksThread.join();
                collectExceptionsThread.join();
                collectStatsRequestsThread.join();
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }
    private List<JsonNode> getCollect(String tasks) {
        return targets.stream().parallel().map(getJsonClientFunction(tasks))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Function<String, JsonNode> getJsonClientFunction(String endpoint) {
        return target -> {
            try {
                URL url = new URL(String.format("%s/%s", target, endpoint));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Reading json from {}", url);
                }
                return new ObjectMapper().readTree(url);
            } catch (IOException e) {
                LOG.error("Cannot retrieve {} for {}", endpoint, target,e);
            }
            return null;
        };
    }
}
