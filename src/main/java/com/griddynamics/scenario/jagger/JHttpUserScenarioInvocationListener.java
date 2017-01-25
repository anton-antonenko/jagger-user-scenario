package com.griddynamics.scenario.jagger;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.AVG_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.FAILS_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.MAX_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.MIN_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.PERCENTILE_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.STD_DEV_AGGREGATOR;
import static com.griddynamics.scenario.jagger.DefaultAggregatorsProvider.SUCCESS_AGGREGATOR;

public class JHttpUserScenarioInvocationListener extends ServicesAware implements Provider<InvocationListener> {

    private final Set<String> createdMetrics = new ConcurrentSkipListSet<>();
    private List<MetricAggregatorProvider> latencyAggregatorProviders = new ArrayList<>();

    public JHttpUserScenarioInvocationListener() {}

    private JHttpUserScenarioInvocationListener(Builder builder) {
        this.latencyAggregatorProviders = newArrayList(builder.latencyAggregatorProviders);
    }

    public static Builder builder() {return new Builder();}

    public static class Builder {
        Set<MetricAggregatorProvider> latencyAggregatorProviders = new HashSet<>();

        public Builder withLatencyAvgStddevAggregators() {
            this.latencyAggregatorProviders.addAll(newArrayList(STD_DEV_AGGREGATOR, AVG_AGGREGATOR));
            return this;
        }

        public Builder withLatencyMinMaxAggregators() {
            this.latencyAggregatorProviders.addAll(newArrayList(MAX_AGGREGATOR, MIN_AGGREGATOR));
            return this;
        }

        public Builder withLatencyPercentileAggregators(Double percentile, Double... percentiles) {
            this.latencyAggregatorProviders.add(PERCENTILE_AGGREGATOR(percentile));
            Arrays.stream(percentiles).forEach(p -> this.latencyAggregatorProviders.add(PERCENTILE_AGGREGATOR(p)));
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
                    JHttpUserScenarioInvocationResult invocationResult = ((JHttpUserScenarioInvocationResult) invocationInfo.getResult());
                    List<JHttpUserScenarioStepInvocationResult> stepInvocationResults = invocationResult.getStepInvocationResults();

                    if (!createdMetrics.contains(invocationResult.getScenarioMetricId())) {
                        createdMetrics.add(invocationResult.getScenarioMetricId());
                        createScenarioDurationMetricDescription(invocationResult.getScenarioMetricId(), invocationResult.getScenarioMetricDisplayName());
                        createScenarioSuccessRateMetricDescription(invocationResult.getScenarioMetricId() + "-ssr", invocationResult.getScenarioMetricDisplayName());
                    }
                    getMetricService().saveValue(invocationResult.getScenarioMetricId(), invocationInfo.getDuration());
                    getMetricService().saveValue(invocationResult.getScenarioMetricId() + "-ssr", invocationResult.getSucceeded() ? 1 : 0);

                    stepInvocationResults.forEach(result -> {
                        String metricId = result.getMetricId();
                        if (!createdMetrics.contains(metricId)) {
                            createdMetrics.add(metricId);
                            String displayName = result.getMetricDisplayName();
                            createStepLatencyMetricDescription(metricId, displayName);
                            createStepSuccessRateMetricDescription(metricId + "-sr", displayName);
                        }
                        getMetricService().saveValue(metricId, result.getLatency());
                        getMetricService().saveValue(metricId + "-sr", result.getSucceeded() ? 1 : 0);
                    });
                }
            }

            private void createScenarioDurationMetricDescription(String metricId, String metricDisplayName) {
                getMetricService().createMetric(new MetricDescription(metricId).displayName(metricDisplayName + ", ms").showSummary(true).plotData(true));
            }

            private void createScenarioSuccessRateMetricDescription(String metricId, String metricDisplayName) {
                MetricDescription metricDescription = new MetricDescription(metricId).displayName(metricDisplayName + " Success Rate").showSummary(true).plotData(true);
                newArrayList(SUCCESS_AGGREGATOR, FAILS_AGGREGATOR).forEach(metricDescription::addAggregator);
                getMetricService().createMetric(metricDescription);
            }

            private void createStepLatencyMetricDescription(String metricId, String displayName) {
                MetricDescription metricDescription = new MetricDescription(metricId).displayName(displayName + " Latency, ms").showSummary(true).plotData(true);
                if (latencyAggregatorProviders.isEmpty())
                    latencyAggregatorProviders.addAll(newHashSet(MAX_AGGREGATOR, MIN_AGGREGATOR, STD_DEV_AGGREGATOR, AVG_AGGREGATOR));
                latencyAggregatorProviders.forEach(metricDescription::addAggregator);
                getMetricService().createMetric(metricDescription);
            }

            private void createStepSuccessRateMetricDescription(String metricId, String displayName) {
                MetricDescription metricDescription = new MetricDescription(metricId).displayName(displayName + " Success rate").showSummary(true).plotData(true);
                newArrayList(SUCCESS_AGGREGATOR, FAILS_AGGREGATOR).forEach(metricDescription::addAggregator);
                getMetricService().createMetric(metricDescription);
            }

            @Override
            public void onFail(InvocationInfo invocationInfo, InvocationException e) { }

            @Override
            public void onError(InvocationInfo invocationInfo, Throwable error) { }
        };
    }
}
