package com.griddynamics.scenario;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.util.CopyUtil;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class JHttpUserScenarioStep {
    private int stepNumber;
    private final String id; // mandatory parameter. required for metrics saving
    private JHttpEndpoint endpoint;
    private JHttpQuery query;
    private JHttpResponse response;
    private final long waitAfterExecutionInSeconds;
    private final String displayName;
    private final Consumer<JHttpUserScenarioStep> previousStepConsumer;
    private final BiConsumer<JHttpUserScenarioStep, JHttpUserScenarioStep> previousAndCurrentStepConsumer;
    private final Function<JHttpResponse, Boolean> responseFunction;

    /**
     * Can work with results from the previous step and set proper values for endpoint & query.
     * @param previousStep previous execution step
     */
    public void preProcess(JHttpUserScenarioStep previousStep) {
        if (previousAndCurrentStepConsumer != null) {
            previousAndCurrentStepConsumer.accept(previousStep, this);
        } else if (previousStepConsumer != null) {
            previousStepConsumer.accept(previousStep);
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
        this.previousStepConsumer = builder.previousStepConsumer;
        this.previousAndCurrentStepConsumer = builder.previousAndCurrentStepConsumer;
        this.responseFunction = builder.responseFunction;
    }

    public static Builder builder(String id, JHttpEndpoint endpoint) {
        return new Builder(id, endpoint);
    }

    public static class Builder {
        private final String id;
        private final JHttpEndpoint endpoint;
        private JHttpQuery query;
        private long waitAfterExecutionInSeconds;
        private String displayName;
        private Consumer<JHttpUserScenarioStep> previousStepConsumer;
        private BiConsumer<JHttpUserScenarioStep, JHttpUserScenarioStep> previousAndCurrentStepConsumer;
        private Function<JHttpResponse, Boolean> responseFunction;

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

        public Builder withPreviousStepConsumer(Consumer<JHttpUserScenarioStep> previousStepConsumer) {
            this.previousStepConsumer = previousStepConsumer;
            return this;
        }

        public Builder withPreviousStepConsumer(BiConsumer<JHttpUserScenarioStep, JHttpUserScenarioStep> previousAndCurrentStepConsumer) {
            this.previousAndCurrentStepConsumer = previousAndCurrentStepConsumer;
            return this;
        }

        public Builder withResponseFunction(Function<JHttpResponse, Boolean> responseFunction) {
            this.responseFunction = responseFunction;
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
        return CopyUtil.copyOf(endpoint);
    }

    /**
     * DON'T USE IT FOR PREVIOUS STEP IN preProcess()
     */
    public void setEndpoint(JHttpEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public JHttpQuery getQuery() {
        return CopyUtil.copyOf(query);
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
}
