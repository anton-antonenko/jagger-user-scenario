package com.griddynamics.scenario;

import java.util.Map;

public class ExampleInvocationResult {
    private final Map<String, Double> requestTimeStorage;
    private final Map<String, String> metricsDisplayName;
    private final String scenarioMetricId;
    private final String scenarioMetricDisplayName;

    public ExampleInvocationResult(Map<String, Double> requestTimeStorage, Map<String, String> metricsDisplayName, String scenarioMetricId, String scenarioMetricDisplayName) {
        this.requestTimeStorage = requestTimeStorage;
        this.metricsDisplayName = metricsDisplayName;
        this.scenarioMetricId = scenarioMetricId;
        this.scenarioMetricDisplayName = scenarioMetricDisplayName;
    }

    public Map<String, Double> getRequestTimeStorage() {
        return requestTimeStorage;
    }

    public Map<String, String> getMetricsDisplayName() {
        return metricsDisplayName;
    }

    public String getScenarioMetricId() {
        return scenarioMetricId;
    }

    public String getScenarioMetricDisplayName() {
        return scenarioMetricDisplayName;
    }
}
