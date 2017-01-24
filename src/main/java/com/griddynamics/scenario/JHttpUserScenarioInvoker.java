package com.griddynamics.scenario;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static com.griddynamics.util.UserStepMetricNameUtil.getMetricDisplayName;
import static com.griddynamics.util.UserStepMetricNameUtil.getMetricId;

public class JHttpUserScenarioInvoker implements Invoker<Void, ExampleInvocationResult, JHttpUserScenario> {

    private final SpringBasedHttpClient httpClient = new SpringBasedHttpClient();

    private static Logger log = LoggerFactory.getLogger(JHttpUserScenarioInvoker.class);

    @Override
    public ExampleInvocationResult invoke(Void nothing, JHttpUserScenario scenario) throws InvocationException {
        JHttpUserScenarioStep previousStep = null;
        HashMap<String, Double> requestTimeStorage = new HashMap<>();
        HashMap<String, String> metricsDisplayName = new HashMap<>();

        for (JHttpUserScenarioStep userScenarioStep : scenario.userScenarioSteps) {
            String id = getMetricId(scenario, userScenarioStep);
            String displayName = getMetricDisplayName(userScenarioStep);
            metricsDisplayName.putIfAbsent(id, displayName);

            userScenarioStep.preProcess(previousStep);

            log.info("Step {}: {}", userScenarioStep.getStepNumber(), userScenarioStep.getId());
            log.info("Endpoint: {}", userScenarioStep.getEndpoint());
            log.info("Query: {}", userScenarioStep.getQuery());

            long requestStartTime = System.nanoTime();
            JHttpResponse response = httpClient.execute(userScenarioStep.getEndpoint(), userScenarioStep.getQuery());
            Double requestTimeInMilliseconds = (System.nanoTime() - requestStartTime) / 1_000_000.0;
            requestTimeStorage.put(id, requestTimeInMilliseconds);

            userScenarioStep.waitAfterExecution();
            Boolean succeeded = userScenarioStep.postProcess(response);
            if (!succeeded) {
                log.warn("Step {} post process returned false! Stopping scenario (next steps won't be executed).", userScenarioStep.getId());
                break;
            }

            previousStep = userScenarioStep;
        }

        return new ExampleInvocationResult(requestTimeStorage, metricsDisplayName, scenario.getScenarioId(), scenario.getScenarioName());
    }
}
