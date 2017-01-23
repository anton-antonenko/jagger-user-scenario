package com.griddynamics.scenario;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class ExampleUserScenarioInvocationListener extends ServicesAware implements Provider<InvocationListener> {

    private final Set<String> createdMetrics = new ConcurrentSkipListSet<>();
    private List<MetricAggregatorProvider> aggregatorProviders = new ArrayList<>();

    public ExampleUserScenarioInvocationListener() {}

    public ExampleUserScenarioInvocationListener(List<MetricAggregatorProvider> aggregatorProviders) {
        this.aggregatorProviders = aggregatorProviders;
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
                    ExampleInvocationResult invocationResult = ((ExampleInvocationResult) invocationInfo.getResult());
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
                getMetricService().createMetric(new MetricDescription(metricId)
                        .displayName(metricDisplayName)
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
