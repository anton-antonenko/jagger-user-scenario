package com.griddynamics.scenario.jagger;

import com.griddynamics.scenario.jagger.JHttpUserScenario;
import com.griddynamics.scenario.jagger.JHttpUserScenarioStep;
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
