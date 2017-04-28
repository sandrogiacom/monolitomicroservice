package com.monolitomicroservice.teste.wildfly.security.mechanism;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.server.handlers.form.FormParserFactory;

public class FormCustomAuthenticationMechanismFactory implements AuthenticationMechanismFactory {
    public static final FormCustomAuthenticationMechanismFactory FACTORY = new FormCustomAuthenticationMechanismFactory();
    private static final Logger LOG = Logger.getLogger(FormCustomAuthenticationMechanismFactory.class.getSimpleName());
    protected static Level LEVEL = Level.INFO;

    @Override
    public AuthenticationMechanism create(String mechanismName, FormParserFactory formParserFactory, Map<String, String> properties) {
        LOG.log(LEVEL, "create(" + mechanismName + ", " + formParserFactory + ", " + properties + ")");
        return new FormCustomAuthenticationMechanism(formParserFactory, mechanismName,
                properties.get(AuthenticationMechanismFactory.LOGIN_PAGE),
                properties.get(AuthenticationMechanismFactory.ERROR_PAGE));
    }
}
