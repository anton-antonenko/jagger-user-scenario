package com.griddynamics.scenario.jagger;

public class UserStepMetricNameUtil {

    // ??? will go to StandardMetricsNamesUtil
    public static String generateScenarioStepId(String scenarioId, String stepId, Integer stepIndex) {
        // both scenario and scenario steps will have same format of ids
        // scenario: USER-SCENARIO_[scenarioId]_STEPNN0_[scenarioId]
        // step:     USER-SCENARIO_[scenarioId]_STEPNN[1...N]_[stepId]
        return "US_" + scenarioId + "_STNN" + stepIndex + "_" + stepId + "_";
    }

    public static String generateScenarioId(String scenarioId) {
        return generateScenarioStepId(scenarioId, scenarioId, 0);
    }

    public static String generateMetricId(String id, String metricId) {
        return id + "METR_" + metricId + "_";
    }

    public static String generateMetricDisplayName(String displayName, String metricDisplayName) {
        return displayName + " " + metricDisplayName;
    }

}
