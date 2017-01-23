package com.griddynamics;

import com.griddynamics.jagger.engine.e1.collector.DefaultResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.JHttpResponseStatusValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.limits.JLimitVsRefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.JMetricName;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowErrThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpErrThresh;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.RequestsPerSecond;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

// begin: following section is used for docu generation - Load test scenario configuration
@Configuration
public class ExampleSimpleJLoadScenarioProvider {

    @Bean
    public JLoadScenario exampleSimpleJaggerLoadScenario_() {

        JTestDefinition jTestDefinition = JTestDefinition.builder(Id.of("td_example"), new ExampleEndpointsProvider()).
                addValidator(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class)).
                build();

        JLoadProfile jLoadProfileRps = JLoadProfileRps.builder(RequestsPerSecond.of(10)).withMaxLoadThreads(10).withWarmUpTimeInMilliseconds(10000).build();
        
        JTerminationCriteria jTerminationCriteria = JTerminationCriteriaIterations.of(IterationsNumber.of(500), MaxDurationInSeconds.of(15));
        
        JLoadTest jLoadTest = JLoadTest.builder(Id.of("lt_example"), jTestDefinition, jLoadProfileRps, jTerminationCriteria).build();


        JTestDefinition jTestDefinitionJaaS = JTestDefinition.builder(Id.of("td_example_jaas"), Arrays.asList(new JHttpEndpoint("http://localhost:8088"))).
                withQueryProvider(Arrays.asList(
                        new JHttpQuery<TestExecutionEntity>().post().
                                path("jaas/executions").
                                body(new TestExecutionEntity() {{setEnvId("1"); setLoadScenarioId("1"); setTestProjectURL("http://123");}}),

                        new JHttpQuery<TestExecutionEntity>().post().
                                path("jaas/executions").
                                body(new TestExecutionEntity() {{setEnvId("2"); setLoadScenarioId("2"); setTestProjectURL("http://123");}}),

                        new JHttpQuery<TestExecutionEntity>().post().
                                path("jaas/error/executions").
                                body(new TestExecutionEntity() {{setEnvId("3"); setLoadScenarioId("3"); setTestProjectURL("http://123");}})
                )).
        addValidator(JHttpResponseStatusValidatorProvider.of(201, 204)).
                        build();

        JLimit jLimitJaaS = JLimitVsRefValue.builder(JMetricName.PERF_SUCCESS_RATE_OK, RefValue.of(1.0)).
                withOnlyErrors(LowErrThresh.of(0.95), UpErrThresh.of(1.00001)).build();

        JLoadTest jLoadTestJaaS = JLoadTest.builder(Id.of("lt_example_jaas"), jTestDefinitionJaaS, jLoadProfileRps, jTerminationCriteria).
                withLimits(jLimitJaaS).build();






        JParallelTestsGroup jParallelTestsGroup = JParallelTestsGroup.builder(Id.of("ptg_example"), jLoadTest, jLoadTestJaaS).build();
        
        // To launch your load scenario, set 'jagger.load.scenario.id.to.execute' property's value equal to the load scenario id
        // You can do it via system properties or in the 'environment.properties' file
        return JLoadScenario.builder(Id.of("ls_example_jaas"), jParallelTestsGroup).build();
    }
}
// end: following section is used for docu generation - Load test scenario configuration

