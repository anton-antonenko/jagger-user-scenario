package com.griddynamics.scenario;

import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.limits.JLimitVsRefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowErrThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpErrThresh;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileInvocation;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.InvocationCount;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.ThreadCount;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import com.griddynamics.scenario.jagger.JHttpUserScenario;
import com.griddynamics.scenario.jagger.JHttpUserScenarioInvocationListener;
import com.griddynamics.scenario.jagger.JHttpUserScenarioInvokerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.AVG_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.MAX_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.MIN_AGGREGATOR;
import static com.griddynamics.scenario.jagger.UserStepMetricNameUtil.getMetricId;
import static java.util.Arrays.asList;

// begin: following section is used for docu generation - Load test scenario configuration
@Configuration
public class ExampleSimpleUserScenarioJLoadScenarioProvider {

    @Bean
    public JLoadScenario exampleSimpleJaggerLoadScenarioUS() {

        //??? don't like this
        ExampleUserScenarioProvider userScenarioProvider = new ExampleUserScenarioProvider();
        JHttpUserScenario userScenario = userScenarioProvider.iterator().next();

        JTestDefinition jTestDefinition = JTestDefinition.builder(Id.of("td_example"), userScenarioProvider)
                .withInvoker(new JHttpUserScenarioInvokerProvider())
                .addListener(new JHttpUserScenarioInvocationListener(asList(AVG_AGGREGATOR, MIN_AGGREGATOR, MAX_AGGREGATOR)))
                .build();

        JLoadProfile jLoadProfileInvocations = JLoadProfileInvocation.builder(InvocationCount.of(10), ThreadCount.of(1)).build();

        JTerminationCriteria jTerminationCriteria = JTerminationCriteriaIterations.of(IterationsNumber.of(500), MaxDurationInSeconds.of(15));

        //??? propose to add option with scenario id string, step id string
        String metricId = getMetricId(userScenario, userScenario.getUserScenarioStep(0));
        JLimit firstStepLimit = JLimitVsRefValue.builder(metricId + "-avg", RefValue.of(300D))
                .withOnlyErrors(LowErrThresh.of(0.99), UpErrThresh.of(1.01))
                .build();

        JLoadTest jLoadTest = JLoadTest.builder(Id.of("lt_example"), jTestDefinition, jLoadProfileInvocations, jTerminationCriteria)
                .withLimits(firstStepLimit)
                .build();

        JParallelTestsGroup jParallelTestsGroup = JParallelTestsGroup.builder(Id.of("ptg_example"), jLoadTest).build();

        // To launch your load scenario, set 'jagger.load.scenario.id.to.execute' property's value equal to the load scenario id
        // You can do it via system properties or in the 'environment.properties' file
        return JLoadScenario.builder(Id.of("ls_example_scenario"), jParallelTestsGroup).build();
    }
}
// end: following section is used for docu generation - Load test scenario configuration

