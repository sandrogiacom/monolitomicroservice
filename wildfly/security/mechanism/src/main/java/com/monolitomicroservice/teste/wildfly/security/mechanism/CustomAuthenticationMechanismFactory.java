package com.monolitomicroservice.teste.wildfly.security.mechanism;

import java.util.Map;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.server.handlers.form.FormParserFactory;

public class CustomAuthenticationMechanismFactory implements AuthenticationMechanismFactory {
    public static final CustomAuthenticationMechanismFactory FACTORY = new CustomAuthenticationMechanismFactory();

    @Override
    public AuthenticationMechanism create(String mechanismName, FormParserFactory formParserFactory, Map<String, String> properties) {
        return new CustomAuthenticationMechanism();
    }
}
