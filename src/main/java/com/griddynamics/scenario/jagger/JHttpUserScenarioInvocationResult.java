package com.griddynamics.scenario.jagger;

import java.util.List;

public class JHttpUserScenarioInvocationResult {
    private final List<JHttpUserScenarioStepInvocationResult> stepInvocationResults;
    private final String scenarioMetricId;
    private final String scenarioMetricDisplayName;
    private final Boolean succeeded;

    public JHttpUserScenarioInvocationResult(List<JHttpUserScenarioStepInvocationResult> stepInvocationResults, String scenarioMetricId, String scenarioMetricDisplayName, Boolean succeeded) {
        this.stepInvocationResults = stepInvocationResults;
        this.scenarioMetricId = scenarioMetricId;
        this.scenarioMetricDisplayName = scenarioMetricDisplayName;
        this.succeeded = succeeded;
    }

    public String getScenarioMetricId() {
        return scenarioMetricId;
    }

    public String getScenarioMetricDisplayName() {
        return scenarioMetricDisplayName;
    }

    public List<JHttpUserScenarioStepInvocationResult> getStepInvocationResults() {
        return stepInvocationResults;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }
}
