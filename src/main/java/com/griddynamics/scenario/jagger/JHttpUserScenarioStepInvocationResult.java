package com.griddynamics.scenario.jagger;

public class JHttpUserScenarioStepInvocationResult {
    private final String metricId;
    private final String metricDisplayName;
    private final Number latency;
    private final Boolean succeeded;

    public JHttpUserScenarioStepInvocationResult(String metricId, String metricDisplayName, Number latency, Boolean succeeded) {
        this.metricId = metricId;
        this.metricDisplayName = metricDisplayName;
        this.latency = latency;
        this.succeeded = succeeded;
    }

    public String getMetricId() {
        return metricId;
    }

    public String getMetricDisplayName() {
        return metricDisplayName;
    }

    public Number getLatency() {
        return latency;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }
}
