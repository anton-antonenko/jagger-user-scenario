package com.griddynamics.scenario.jagger;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class UserStepMetricNameUtil {
    public static String getMetricId(JHttpUserScenario scenario, JHttpUserScenarioStep userScenarioStep) {
        return getMetricId(scenario.getScenarioId(), userScenarioStep.getId(), userScenarioStep.getStepNumber());
    }

    public static String getMetricId(String scenarioId, String userScenarioStepId, int userScenarioNumber) {
        String prefix = "USER-SCENARIO_" + scenarioId + "_STEP#" + userScenarioNumber + "_";
        return prefix + userScenarioStepId;
    }

    public static String getMetricDisplayName(JHttpUserScenarioStep userScenarioStep) {
        String prefix = userScenarioStep.getStepNumber() + ". ";
        return prefix + (isBlank(userScenarioStep.getDisplayName()) ? userScenarioStep.getId() : userScenarioStep.getDisplayName()) + ", ms";
    }
}
