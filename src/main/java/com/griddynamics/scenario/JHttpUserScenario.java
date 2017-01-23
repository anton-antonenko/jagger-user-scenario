package com.griddynamics.scenario;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class JHttpUserScenario {
    private Integer stepsCounter = 0;
    private final String scenarioId;
    private final String scenarioName;
    List<JHttpUserScenarioStep> userScenarioSteps = new ArrayList<>();

    public JHttpUserScenario(String scenarioId, String scenarioName) {
        this.scenarioId = scenarioId;
        this.scenarioName = scenarioName;
    }

    public JHttpUserScenario addStep (JHttpUserScenarioStep userScenarioStep) {
        if (!isStepIdUnique(userScenarioStep.getId())) {
            throw new IllegalArgumentException(format("Step id '%s' is not unique!", userScenarioStep.getId()));
        }
        stepsCounter++;
        userScenarioStep.setStepNumber(stepsCounter);
        userScenarioSteps.add(userScenarioStep);
        return this;
    }

    private boolean isStepIdUnique(String id) {
        return userScenarioSteps.stream().map(JHttpUserScenarioStep::getId).noneMatch(stepId -> stepId.equals(id));
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public JHttpUserScenarioStep getUserScenarioStep(int index) {
        return userScenarioSteps.get(index);
    }
}
