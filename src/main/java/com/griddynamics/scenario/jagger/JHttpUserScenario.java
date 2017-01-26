package com.griddynamics.scenario.jagger;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class JHttpUserScenario {
    private Integer stepsCounter = 0;
    private final String scenarioId;
    private final String scenarioName;
    private String userName;
    private String password;
    List<JHttpUserScenarioStep> userScenarioSteps = new ArrayList<>();
    private JHttpEndpoint globalEndpoint;

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

    //??? add builder in the future
    public JHttpUserScenario withBasicAuth(String userName, String password) {
        this.userName = userName;
        this.password = password;
        return this;
    }

    /** Sets endpoint for all steps.
     * Endpoint still can be overridden in {@link JHttpUserScenarioStep.Builder#withPreProcessFunction}
     * @param globalEndpoint global endpoint to set
     */
    public JHttpUserScenario withGlobalEndpoint(JHttpEndpoint globalEndpoint) {
        this.globalEndpoint = globalEndpoint;
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

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public JHttpEndpoint getGlobalEndpoint() {
        return globalEndpoint;
    }
}
