package com.griddynamics.scenario;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class JHttpUserScenarioInvoker implements Invoker<Void, ExampleInvocationResult, JHttpUserScenario> {

    private final SpringBasedHttpClient httpClient = new SpringBasedHttpClient();

    private static Logger log = LoggerFactory.getLogger(JHttpUserScenarioInvoker.class);

    @Override
    public ExampleInvocationResult invoke(Void nothing, JHttpUserScenario scenario) throws InvocationException {
        JHttpUserScenarioStep previousStep = null;
        HashMap<String, Double> requestTimeStorage = new HashMap<>();
        HashMap<String, String> metricsDisplayName = new HashMap<>();

        for (JHttpUserScenarioStep userScenarioStep : scenario.userScenarioSteps) {
            String displayName = getDisplayName(scenario, userScenarioStep);
            metricsDisplayName.putIfAbsent(userScenarioStep.getId(), displayName);

            userScenarioStep.preProcess(previousStep);

            log.info("Endpoint: {}", userScenarioStep.getEndpoint());
            log.info("Query: {}", userScenarioStep.getQuery());

            long requestStartTime = System.nanoTime();
            JHttpResponse response = httpClient.execute(userScenarioStep.getEndpoint(), userScenarioStep.getQuery());
            long requestEndTime = System.nanoTime();
            Double requestTimeInMilliseconds = (requestEndTime - requestStartTime) / 1_000_000.0;

            requestTimeStorage.put(userScenarioStep.getId(), requestTimeInMilliseconds);

            userScenarioStep.waitAfterExecution();
            userScenarioStep.postProcess(response);

            previousStep = userScenarioStep;
        }

        return new ExampleInvocationResult(requestTimeStorage, metricsDisplayName, scenario.getScenarioId(), scenario.getScenarioName());
    }

    private String getDisplayName(JHttpUserScenario scenario, JHttpUserScenarioStep userScenarioStep) {
        String prefix = scenario.getScenarioName() + "-" + userScenarioStep.getStepNumber() + "-";
        return StringUtils.isBlank(userScenarioStep.getDisplayName()) ? prefix + userScenarioStep.getId() : prefix + userScenarioStep.getDisplayName();
    }
}
