package com.griddynamics.util;

import com.griddynamics.scenario.JHttpUserScenario;
import com.griddynamics.scenario.JHttpUserScenarioStep;
import org.apache.commons.lang3.StringUtils;

public class UserStepMetricNameUtil {
    public static String getMetricId(JHttpUserScenario scenario, JHttpUserScenarioStep userScenarioStep) {
        String prefix = scenario.getScenarioId() + "-" + userScenarioStep.getStepNumber() + "-";
        return prefix + userScenarioStep.getId();
    }

    public static String getMetricDisplayName(JHttpUserScenarioStep userScenarioStep) {
        String prefix = userScenarioStep.getStepNumber() + ". ";
        return StringUtils.isBlank(userScenarioStep.getDisplayName()) ? prefix + userScenarioStep.getId() : prefix + userScenarioStep.getDisplayName();
    }
}
