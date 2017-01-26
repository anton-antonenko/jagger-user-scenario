package com.griddynamics.scenario.jagger;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.griddynamics.scenario.jagger.UserStepMetricNameUtil.getMetricDisplayName;
import static com.griddynamics.scenario.jagger.UserStepMetricNameUtil.getMetricId;

public class JHttpUserScenarioInvoker implements Invoker<Void, JHttpUserScenarioInvocationResult, JHttpUserScenario> {

    private final SpringBasedHttpClient httpClient = new SpringBasedHttpClient();

    private static Logger log = LoggerFactory.getLogger(JHttpUserScenarioInvoker.class);

    @Override
    public JHttpUserScenarioInvocationResult invoke(Void nothing, JHttpUserScenario scenario) throws InvocationException {
        Boolean scenarioSucceeded = true;
        JHttpUserScenarioStep previousStep = null;
        List<JHttpUserScenarioStepInvocationResult> stepInvocationResults = new ArrayList<>();
        JHttpEndpoint globalEndpoint = scenario.getGlobalEndpoint();

        for (JHttpUserScenarioStep userScenarioStep : scenario.userScenarioSteps) {
            String metricId = getMetricId(scenario, userScenarioStep);
            String metricDisplayName = getMetricDisplayName(userScenarioStep);

            // Pre process step: internal setup. Can be later overridden by the user
            // Basic auth
            if (scenario.getPassword() != null && scenario.getUserName() != null) {
                String value = Base64.getEncoder().encodeToString((scenario.getUserName() + ":" + scenario.getPassword()).getBytes());
                userScenarioStep.getQuery().header("Authorization", "Basic " + value);
            }

            // use global endpoint for step if present
            if (globalEndpoint != null)
                userScenarioStep.setEndpoint(globalEndpoint);
            // check endpoint for null
            if (globalEndpoint == null && userScenarioStep.getEndpoint() == null)
                throw new IllegalArgumentException("Endpoint must not be null! Please, set global endpoint or set endpoint for every step.");

            // Pre process step: user actions executed before request
            userScenarioStep.preProcess(previousStep);

            log.info("Step {}: {}", userScenarioStep.getStepNumber(), userScenarioStep.getId());
            log.info("Endpoint: {}", userScenarioStep.getEndpoint());
            log.info("Query: {}", userScenarioStep.getQuery());

            // Request execution step
            long requestStartTime = System.nanoTime();
            JHttpResponse response = httpClient.execute(userScenarioStep.getEndpoint(), userScenarioStep.getQuery());
            Double requestTimeInMilliseconds = (System.nanoTime() - requestStartTime) / 1_000_000.0;

            //??? need to decide how to setup detailed output. Response can be very large
            log.info("Response: {}", response);

            // Wait after execution if need
            userScenarioStep.waitAfterExecution();

            // Post process step: executed after request. If returned false, scenario invocation stops.
            Boolean succeeded = userScenarioStep.postProcess(response);
            stepInvocationResults.add(new JHttpUserScenarioStepInvocationResult(metricId, metricDisplayName, requestTimeInMilliseconds, succeeded));
            previousStep = userScenarioStep;

            if (!succeeded) {
                scenarioSucceeded = false;
                log.error("Step {} post process returned false! Stopping scenario (next steps won't be executed).", userScenarioStep.getId());
                break;
            }
        }

        return new JHttpUserScenarioInvocationResult(stepInvocationResults, scenario.getScenarioId(), scenario.getScenarioName(), scenarioSucceeded);
    }
}
