package com.griddynamics.scenario;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.scenario.jagger.JHttpUserScenario;
import com.griddynamics.scenario.jagger.JHttpUserScenarioStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExampleUserScenarioProvider implements Iterable {
    public static final String SCENARIO_ID = "my-user-scenario";
    public static final String SCENARIO_DISPLAY_NAME = "My User Scenario";
    public static final String STEP_1_ID = "step321";
    public static final String STEP_2_ID = "step2";
    public static final String STEP_3_ID = "step3";

    public static final String SCENARIO_ID_AUTH_AUTO = "my-user-scenario-basic-auth-auto";
    public static final String SCENARIO_DISPLAY_NAME_AUTH_AUTO = "Basic Auth User Scenario";

    private static Logger log = LoggerFactory.getLogger(ExampleUserScenarioProvider.class);

    private List<JHttpUserScenario> userScenarios = new ArrayList<>();

    public ExampleUserScenarioProvider() {
        JHttpUserScenario userScenario = new JHttpUserScenario(SCENARIO_ID, SCENARIO_DISPLAY_NAME);

        userScenario
                .addStep(JHttpUserScenarioStep.builder(STEP_1_ID, new JHttpEndpoint("https://httpbin.org/"))
                        .withDisplayName("Step #321")
                        .withWaitAfterExecutionInSeconds(1)
                        .build())
                .addStep(JHttpUserScenarioStep.builder(STEP_2_ID, new JHttpEndpoint("https://httpbin.org/"))
                        .withDisplayName("Step #2")
                        .withQuery(new JHttpQuery().get().path("/get"))
                        .withPostProcessFunction(response -> {
                            if (response.getStatus().is2xxSuccessful())
                                log.info("Step 2 is successful!");
                            return true;
                        })
                        .build())
                .addStep(JHttpUserScenarioStep.builder(STEP_3_ID, new JHttpEndpoint("https://httpbin.org/"))
                        .withDisplayName("Step #3")
                        .withQuery(new JHttpQuery().get().path("/response-headers?key=val"))
                        // VERY IMPORTANT: if use withPreProcessFunction(BiConsumer) arguments order of lambda must be exactly
                        // (prevStep, currentStep) and not (currentStep, prevStep)!!!
                        .withPreProcessFunction((prevStep, currentStep) -> {
                            // Query and Endpoint of current step can be reset here using prevStep.response values
                            currentStep.setEndpoint(new JHttpEndpoint("http://www.scala-lang.org"));
                            currentStep.setQuery(new JHttpQuery().get().path("/"));
                        })
                        .build());

        JHttpUserScenario userScenarioBasicAuthAuto = new JHttpUserScenario(SCENARIO_ID_AUTH_AUTO, SCENARIO_DISPLAY_NAME_AUTH_AUTO);

        userScenarioBasicAuthAuto
                .withBasicAuth("userName","userPassword")
                .addStep(JHttpUserScenarioStep.builder("basic_auto_1", new JHttpEndpoint("https://httpbin.org"))
                        .withQuery(new JHttpQuery().get().path("/basic-auth/userName/userPassword"))
                        .withDisplayName("Expected auth pass")
                        .withWaitAfterExecutionInSeconds(2)
                        .build())
                .addStep(JHttpUserScenarioStep.builder("basic_auto_2", new JHttpEndpoint("https://httpbin.org"))
                        .withQuery(new JHttpQuery().get().path("/basic-auth/userName/userPassword"))
                        .withDisplayName("Expected auth pass with validation")
                        .withWaitAfterExecutionInSeconds(2)
                        .withPostProcessFunction(response -> {
                            Boolean result = true;
                            if (response.getStatus().value() != 200) {
                                log.error("Unexpected status returned. Expected: 200. Returned: {}", response.getStatus().value());
                                result = false;
                            }
                            return result;
                        })
                        .build())
                .addStep(JHttpUserScenarioStep.builder("basic_auto_3", new JHttpEndpoint("https://httpbin.org"))
                        .withQuery(new JHttpQuery().get().path("/basic-auth/userName/userPassword"))
                        .withDisplayName("Expected auth fail with validation")
                        .withWaitAfterExecutionInSeconds(2)
                        .withPreProcessFunction((prevStep, currentStep) -> {
                            // Reset all headers => auth will fail
                            currentStep.getQuery().headers(null);
                        })
                        .withPostProcessFunction(response -> {
                            Boolean result = true;
                            if (response.getStatus().value() != 401) {
                                log.error("Unexpected status returned. Expected: 401. Returned: {}", response.getStatus().value());
                                result = false;
                            }
                            return result;
                        })
                        .build())

        ;

        userScenarios.add(userScenario);
        userScenarios.add(userScenarioBasicAuthAuto);
    }

    @Override
    public Iterator<JHttpUserScenario> iterator() {
        return userScenarios.iterator();
    }
}
