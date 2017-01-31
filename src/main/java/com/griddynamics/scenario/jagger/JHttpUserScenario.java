package com.griddynamics.scenario.jagger;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class JHttpUserScenario {
    private Integer stepsCounter = 0;
    private final String scenarioId;
    private final String scenarioName;
    private List<JHttpUserScenarioStep> userScenarioSteps = new ArrayList<>();
    private JHttpScenarioGlobalContext scenarioGlobalContext;

    public JHttpUserScenario(String scenarioId, String scenarioName) {
        this.scenarioId = scenarioId;
        this.scenarioName = scenarioName;
    }

    public JHttpUserScenario withScenarioGlobalContext(JHttpScenarioGlobalContext scenarioGlobalContext) {
        this.scenarioGlobalContext = scenarioGlobalContext;
        return this;
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

    public List<JHttpUserScenarioStep> getUserScenarioSteps() {
        return userScenarioSteps;
    }

    public JHttpUserScenarioStep getUserScenarioStep(int index) {
        return userScenarioSteps.get(index);
    }

    public JHttpScenarioGlobalContext getScenarioGlobalContext() {
        return scenarioGlobalContext;
    }
}
