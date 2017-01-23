package com.griddynamics.scenario;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JHttpUserScenarioInvokerProvider implements Provider<Invoker>  {

    private static final Logger log = LoggerFactory.getLogger(JHttpUserScenarioInvokerProvider.class);

    @Override
    public Invoker provide() {
        return new JHttpUserScenarioInvoker();
    }
}