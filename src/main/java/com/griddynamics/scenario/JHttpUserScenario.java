package com.griddynamics.scenario;

import java.util.ArrayList;
import java.util.List;

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
        stepsCounter++;
        userScenarioStep.setStepNumber(stepsCounter);
        userScenarioSteps.add(userScenarioStep);
        return this;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public JHttpUserScenarioStep getUserScenario(int index) {
        return userScenarioSteps.get(index);
    }
}
