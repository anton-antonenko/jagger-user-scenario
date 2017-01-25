package com.griddynamics.scenario.jagger;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;

import static com.griddynamics.scenario.jagger.UserStepMetricNameUtil.getMetricDisplayName;
import static com.griddynamics.scenario.jagger.UserStepMetricNameUtil.getMetricId;

public class JHttpUserScenarioInvoker implements Invoker<Void, JHttpUseScenarioStepInvocationResult, JHttpUserScenario> {

    private final SpringBasedHttpClient httpClient = new SpringBasedHttpClient();

    private static Logger log = LoggerFactory.getLogger(JHttpUserScenarioInvoker.class);

    @Override
    public JHttpUseScenarioStepInvocationResult invoke(Void nothing, JHttpUserScenario scenario) throws InvocationException {
        JHttpUserScenarioStep previousStep = null;
        HashMap<String, Double> requestTimeStorage = new HashMap<>();
        HashMap<String, String> metricsDisplayName = new HashMap<>();

        for (JHttpUserScenarioStep userScenarioStep : scenario.userScenarioSteps) {
            String id = getMetricId(scenario, userScenarioStep);
            String displayName = getMetricDisplayName(userScenarioStep);
            metricsDisplayName.putIfAbsent(id, displayName);

            // Pre process step: internal setup. Can be later overridden by the user
            // Basic auth
            if (scenario.getPassword() != null && scenario.getUserName() != null) {
                //??? do we really need copyOf for queries and endpoints?
                JHttpQuery query = userScenarioStep.getQuery().header("Authorization","Basic " +
                        Base64.getEncoder().
                                encodeToString((scenario.getUserName() + ":" + scenario.getPassword()).getBytes()));
                userScenarioStep.setQuery(query);
            }

            // Pre process step: user actions executed before request
            userScenarioStep.preProcess(previousStep);

            log.info("Step {}: {}", userScenarioStep.getStepNumber(), userScenarioStep.getId());
            log.info("Endpoint: {}", userScenarioStep.getEndpoint());
            log.info("Query: {}", userScenarioStep.getQuery());

            // Request execution step
            long requestStartTime = System.nanoTime();
            JHttpResponse response = httpClient.execute(userScenarioStep.getEndpoint(), userScenarioStep.getQuery());
            Double requestTimeInMilliseconds = (System.nanoTime() - requestStartTime) / 1_000_000.0;
            requestTimeStorage.put(id, requestTimeInMilliseconds);

            //??? need to decide how to setup detailed output. Response can be very large
            log.info("Response: {}", response);

            // Wait after execution if need
            userScenarioStep.waitAfterExecution();

            // Post process step: executed after request. If returned false, scenario invocation stops.
            Boolean succeeded = userScenarioStep.postProcess(response);
            if (!succeeded) {
                log.error("Step {} post process returned false! Stopping scenario (next steps won't be executed).", userScenarioStep.getId());
                break;
            }

            previousStep = userScenarioStep;
        }

        //??? success rate is not collected :)
        //??? success rate should not have ' ,ms'
        //??? additional metric per step - Iterations
        //??? need additional validator for scenario success rate
        return new JHttpUseScenarioStepInvocationResult(requestTimeStorage, metricsDisplayName, scenario.getScenarioId(), scenario.getScenarioName());
    }
}
