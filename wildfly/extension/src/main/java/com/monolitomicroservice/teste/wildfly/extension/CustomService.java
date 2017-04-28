package com.monolitomicroservice.teste.wildfly.extension;

import java.util.Map;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.wildfly.extension.undertow.ServletContainerService;

import com.monolitomicroservice.teste.wildfly.security.mechanism.FormCustomAuthenticationMechanism;
import com.monolitomicroservice.teste.wildfly.security.mechanism.FormCustomAuthenticationMechanismFactory;

import io.undertow.security.api.AuthenticationMechanismFactory;

public class CustomService implements Service<CustomService> {
    protected final InjectedValue<ServletContainerService> servletContainer = new InjectedValue<>();

    @Override
    public void start(StartContext startContext) throws StartException {
        //Instala o "CustomAuth" no container
        Map<String, AuthenticationMechanismFactory> authMethods = servletContainer.getValue()
                .getAuthenticationMechanisms();
        //authMethods.put("CUSTOMAUTH", CustomAuthenticationMechanismFactory.FACTORY);
        authMethods.put(FormCustomAuthenticationMechanism.MECHANISM_NAME, FormCustomAuthenticationMechanismFactory.FACTORY);
        //
    }

    @Override
    public void stop(StopContext stopContext) {
    }

    @Override
    public CustomService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    public static ServiceName createServiceName() {
        return ServiceName.JBOSS.append("custom");
    }
}
