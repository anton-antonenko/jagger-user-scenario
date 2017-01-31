package com.griddynamics.scenario.jagger;

import java.util.List;

public class JHttpUserScenarioInvocationResult {
    private final List<JHttpUserScenarioStepInvocationResult> stepInvocationResults;
    private final String scenarioId;
    private final String scenarioDisplayName;
    private final Boolean succeeded;

    public JHttpUserScenarioInvocationResult(List<JHttpUserScenarioStepInvocationResult> stepInvocationResults, String scenarioId, String scenarioDisplayName, Boolean succeeded) {
        this.stepInvocationResults = stepInvocationResults;
        this.scenarioId = scenarioId;
        this.scenarioDisplayName = scenarioDisplayName;
        this.succeeded = succeeded;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public String getScenarioDisplayName() {
        return scenarioDisplayName;
    }

    public List<JHttpUserScenarioStepInvocationResult> getStepInvocationResults() {
        return stepInvocationResults;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }
}
