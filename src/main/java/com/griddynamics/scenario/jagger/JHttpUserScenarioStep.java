package com.griddynamics.scenario.jagger;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class JHttpUserScenarioStep {
    private int stepNumber;
    private final String id; // mandatory parameter. required for metrics saving
    private JHttpEndpoint endpoint;
    private JHttpQuery query;
    private JHttpResponse response;
    private final long waitAfterExecutionInSeconds;
    private final String displayName;
    private final BiConsumer<JHttpUserScenarioStep, JHttpUserScenarioStep> previousAndCurrentStepConsumer;
    private final Function<JHttpResponse, Boolean> responseFunction;
    private JHttpEndpoint globalEndpoint;

    /**
     * Can work with results from the previous step and set proper values for endpoint & query.
     * @param previousStep previous execution step
     */
    public void preProcess(JHttpUserScenarioStep previousStep) {
        if (previousAndCurrentStepConsumer != null) {
            previousAndCurrentStepConsumer.accept(previousStep, this);
        }
    }

    /** Can work with response.
     * @param response result of execution of request
     */
    public Boolean postProcess(JHttpResponse response) {
        this.response = response;
        if (responseFunction != null)
            return responseFunction.apply(CopyUtil.copyOf(response));
        return true;
    }

    public void waitAfterExecution() {
        if (waitAfterExecutionInSeconds > 0) {
            try {
                TimeUnit.SECONDS.sleep(waitAfterExecutionInSeconds);
            } catch (InterruptedException e) {
                throw new RuntimeException("Error occurred while waiting after execution", e);
            }
        }
    }

    private JHttpUserScenarioStep(Builder builder) {
        this.id = builder.id;
        this.endpoint = builder.endpoint;
        this.query = builder.query;
        this.waitAfterExecutionInSeconds = builder.waitAfterExecutionInSeconds;
        this.displayName = builder.displayName;
        this.previousAndCurrentStepConsumer = builder.previousAndCurrentStepConsumer;
        this.responseFunction = builder.responseFunction;
        this.globalEndpoint = builder.globalEndpoint;
    }

    public static Builder builder(String id, JHttpEndpoint endpoint) {
        return new Builder(id, endpoint);
    }

    /** Use this method only if you set global endpoint!!!
     * @param id step id
     */
    public static Builder builder(String id) {
        return new Builder(id, null);
    }

    public static class Builder {
        private final String id;
        private final JHttpEndpoint endpoint;
        private JHttpQuery query;
        private long waitAfterExecutionInSeconds;
        private String displayName;
        private BiConsumer<JHttpUserScenarioStep, JHttpUserScenarioStep> previousAndCurrentStepConsumer;
        private Function<JHttpResponse, Boolean> responseFunction;
        private JHttpEndpoint globalEndpoint;

        private Builder(String id, JHttpEndpoint endpoint) {
            this.id = id;
            this.endpoint = endpoint;
        }

        public Builder withQuery(JHttpQuery query) {
            this.query = query;
            return this;
        }

        public Builder withWaitAfterExecutionInSeconds(long waitAfterExecutionInSeconds) {
            this.waitAfterExecutionInSeconds = waitAfterExecutionInSeconds;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withPreProcessFunction(BiConsumer<JHttpUserScenarioStep, JHttpUserScenarioStep> previousAndCurrentStepConsumer) {
            this.previousAndCurrentStepConsumer = previousAndCurrentStepConsumer;
            return this;
        }

        public Builder withPostProcessFunction(Function<JHttpResponse, Boolean> responseFunction) {
            this.responseFunction = responseFunction;
            return this;
        }

        /** Sets endpoint for current and next steps.
         * Endpoint still can be overridden in {@link Builder#withPreProcessFunction}
         * @param globalEndpoint global endpoint to set
         */
        public Builder withGlobalEndpoint(JHttpEndpoint globalEndpoint) {
            this.globalEndpoint = globalEndpoint;
            return this;
        }

        public JHttpUserScenarioStep build() {
            return new JHttpUserScenarioStep(this);
        }
    }

    public long getWaitAfterExecutionInSeconds() {
        return waitAfterExecutionInSeconds;
    }

    public JHttpEndpoint getEndpoint() {
        return endpoint;
    }

    /**
     * DON'T USE IT FOR PREVIOUS STEP IN preProcess()
     */
    public void setEndpoint(JHttpEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public JHttpQuery getQuery() {
        return query;
    }

    /**
     * DON'T USE IT FOR PREVIOUS STEP IN preProcess()
     */
    public void setQuery(JHttpQuery query) {
        this.query = query;
    }

    public JHttpResponse getResponse() {
        return CopyUtil.copyOf(response);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public JHttpEndpoint getGlobalEndpoint() {
        return globalEndpoint;
    }
}
