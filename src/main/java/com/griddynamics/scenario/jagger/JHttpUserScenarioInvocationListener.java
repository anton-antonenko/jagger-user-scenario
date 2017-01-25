package com.griddynamics.scenario.jagger;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.AVG_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.FAILS_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.MAX_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.MIN_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.STD_DEV_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.SUCCESS_AGGREGATOR;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;

public class JHttpUserScenarioInvocationListener extends ServicesAware implements Provider<InvocationListener> {

    private final Set<String> createdMetrics = new ConcurrentSkipListSet<>();
    private List<MetricAggregatorProvider> aggregatorProviders = new ArrayList<>();

    public JHttpUserScenarioInvocationListener() {}

    private JHttpUserScenarioInvocationListener(Builder builder) {
        this.aggregatorProviders = newArrayList(builder.aggregatorProviders);
    }

    public static Builder builder() {return new Builder();}

    public static class Builder {
        Set<MetricAggregatorProvider> aggregatorProviders = new HashSet<>();

        public Builder withAggregatorProviders(List<MetricAggregatorProvider> aggregatorProviders) {
            this.aggregatorProviders.addAll(aggregatorProviders);
            return this;
        }

        public Builder withLatencyAggregators() {
            this.aggregatorProviders.addAll(newArrayList(MAX_AGGREGATOR, MIN_AGGREGATOR, STD_DEV_AGGREGATOR, AVG_AGGREGATOR));
            return this;
        }

        public Builder withSuccessRateAggregators() {
            this.aggregatorProviders.addAll(newArrayList(SUCCESS_AGGREGATOR, FAILS_AGGREGATOR));
            return this;
        }

        public JHttpUserScenarioInvocationListener build() {
            return new JHttpUserScenarioInvocationListener(this);
        }
    }


    @Override
    protected void init() {}

    @Override
    public InvocationListener provide() {
        return new InvocationListener() {
            @Override
            public void onStart(InvocationInfo invocationInfo) { }

            @Override
            public void onSuccess(InvocationInfo invocationInfo) {
                if (invocationInfo.getResult() != null) {
                    JHttpUseScenarioStepInvocationResult invocationResult = ((JHttpUseScenarioStepInvocationResult) invocationInfo.getResult());
                    Map<String, String> metricsDisplayName = invocationResult.getMetricsDisplayName();

                    if (!createdMetrics.contains(invocationResult.getScenarioMetricId())) {
                        createdMetrics.add(invocationResult.getScenarioMetricId());
                        createScenarioMetricDescription(invocationResult.getScenarioMetricId(), invocationResult.getScenarioMetricDisplayName());
                    }
                    getMetricService().saveValue(invocationResult.getScenarioMetricId(), invocationInfo.getDuration());

                    invocationResult.getRequestTimeStorage().forEach((metricId, value) -> {
                        String metricDisplayName = metricsDisplayName.get(metricId);
                        if (!createdMetrics.contains(metricId)) {
                            createdMetrics.add(metricId);
                            createStepMetricDescription(metricId, metricDisplayName);
                        }
                        getMetricService().saveValue(metricId, value);
                    });
                }
            }

            private void createScenarioMetricDescription(String metricId, String metricDisplayName) {
                String suffix = ", ms";
                String displayName = endsWithIgnoreCase(metricDisplayName, suffix) ? metricDisplayName : metricDisplayName + suffix;
                getMetricService().createMetric(new MetricDescription(metricId)
                        .displayName(displayName)
                        .showSummary(true)
                        .plotData(true)
                );
            }

            private void createStepMetricDescription(String metricId, String metricDisplayName) {
                MetricDescription metricDescription = new MetricDescription(metricId).displayName(metricDisplayName).showSummary(true).plotData(true);
                aggregatorProviders.forEach(metricDescription::addAggregator);
                getMetricService().createMetric(metricDescription);
            }

            @Override
            public void onFail(InvocationInfo invocationInfo, InvocationException e) { }

            @Override
            public void onError(InvocationInfo invocationInfo, Throwable error) { }
        };
    }
}
