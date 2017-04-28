package com.monolitomicroservice.teste.wildfly.security.mechanism;

import java.security.Principal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.servlet.handlers.ServletRequestContext;

public class CustomAuthenticationMechanism implements AuthenticationMechanism {
    private static final Logger LOG = Logger.getLogger(CustomAuthenticationMechanism.class.getSimpleName());
    protected static Level LEVEL = Level.FINEST;

    private FormParserFactory formParserFactory;
    private Map<String, String> properties;

    public CustomAuthenticationMechanism(FormParserFactory formParserFactory, Map<String, String> properties) {
        this.formParserFactory = formParserFactory;
        this.properties = properties;
    }

    @Override
    public AuthenticationMechanismOutcome authenticate(HttpServerExchange httpServerExchange, SecurityContext securityContext) {
        LOG.log(LEVEL, "authenticate(" + httpServerExchange + ", " + securityContext + "): isAuthenticationRequired=" + securityContext.isAuthenticationRequired());

        if (!securityContext.isAuthenticationRequired()) {
            return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
        }

        ServletRequestContext servletRequestContext = httpServerExchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        HttpServletRequest request = servletRequestContext.getOriginalRequest();

        Principal principal = request.getUserPrincipal();
        LOG.log(LEVEL, "authenticate() - principal=" + principal);

        if (principal != null && principal.getName() != null) {
            LOG.log(LEVEL, "authenticate() - result: " + AuthenticationMechanismOutcome.AUTHENTICATED);
            return AuthenticationMechanismOutcome.AUTHENTICATED;
        }

        LOG.log(LEVEL, "authenticate() - result: " + AuthenticationMechanismOutcome.AUTHENTICATED);
        return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }

    @Override
    public ChallengeResult sendChallenge(HttpServerExchange httpServerExchange, SecurityContext securityContext) {
        LOG.log(LEVEL, "sendChallenge(httpServerExchange=" + httpServerExchange
                + ", securityContext=" + securityContext + ") - isAuthenticationRequired="
                + securityContext.isAuthenticationRequired());
        return new ChallengeResult(true, HttpServletResponse.SC_UNAUTHORIZED);
    }
}
