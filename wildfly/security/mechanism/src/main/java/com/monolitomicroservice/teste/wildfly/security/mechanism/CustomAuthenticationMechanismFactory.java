package com.monolitomicroservice.teste.wildfly.security.mechanism;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.server.handlers.form.FormParserFactory;

public class CustomAuthenticationMechanismFactory implements AuthenticationMechanismFactory {
    public static final CustomAuthenticationMechanismFactory FACTORY = new CustomAuthenticationMechanismFactory();
    private static final Logger LOG = Logger.getLogger(CustomAuthenticationMechanismFactory.class.getName());
    protected static Level LEVEL = Level.FINEST;

    @Override
    public AuthenticationMechanism create(String mechanismName, FormParserFactory formParserFactory, Map<String, String> properties) {
        LOG.log(LEVEL, "create(" + mechanismName + ", " + formParserFactory + ", " + properties + ")");
        return new CustomAuthenticationMechanism(formParserFactory, properties);
    }
}
