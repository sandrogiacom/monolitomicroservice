package com.monolitomicroservice.teste.wildfly.security.mechanism;

import static io.undertow.UndertowMessages.MESSAGES;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.monolitomicroservice.teste.wildfly.security.common.jwt.JwtManager;

import io.undertow.UndertowLogger;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import io.undertow.security.impl.FormAuthenticationMechanism;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.handlers.security.ServletFormAuthenticationMechanism;

public class FormCustomAuthenticationMechanism extends ServletFormAuthenticationMechanism {
    public static final String MECHANISM_NAME = "FORM";

    private static final Logger LOG = Logger.getLogger(FormCustomAuthenticationMechanism.class.getSimpleName());
    protected static Level LEVEL = Level.INFO;

    public FormCustomAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage) {
        super(formParserFactory, name, loginPage, errorPage, FormAuthenticationMechanism.DEFAULT_POST_LOCATION);
        LOG.log(LEVEL, "FormCustomAuthenticationMechanism(" + formParserFactory + ", " + name + ", " + loginPage + ", " + errorPage + ")");
    }

    @Override
    public AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
        LOG.log(LEVEL, "authenticate(" + exchange + ", " + securityContext + "): isAuthenticationRequired=" + securityContext.isAuthenticationRequired());

        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        LOG.log(LEVEL, "authenticate - servletRequestContext=" + servletRequestContext);

        HttpServletRequest request = servletRequestContext.getOriginalRequest();
        LOG.log(LEVEL, "authenticate - request=" + request);

        Principal principal = request.getUserPrincipal();
        LOG.log(LEVEL, "authenticate - principal=" + principal);
        if (principal != null) {
            return AuthenticationMechanismOutcome.AUTHENTICATED;
        }

        HttpServletResponse response = servletRequestContext.getOriginalResponse();
        LOG.log(LEVEL, "authenticate - response=" + response);

        request.setAttribute(HttpServerExchange.class.getName(), exchange);

        String token = JwtManager.getBearerToken(request);
        if (token != null) {
            // Autentica via token
            LOG.log(LEVEL, "&&&&&&&&&& authenticate - token=" + token);
            LOG.log(LEVEL, "&&&&&&&&&& authenticate - vai logar via JWT");

            AuthenticationMechanismOutcome outcome = null;
            PasswordCredential credential = new PasswordCredential(token.toCharArray());
            try {
                IdentityManager identityManager = securityContext.getIdentityManager();
                Account account = identityManager.verify(token, credential);
                LOG.log(LEVEL, "&&&&&&&&&& authenticate - account=" + account);
                if (account != null) {
                    securityContext.authenticationComplete(account, MECHANISM_NAME, true);
                    UndertowLogger.SECURITY_LOGGER.debugf("Authenticated user %s using for auth for %s", account.getPrincipal().getName(), exchange);
                    outcome = AuthenticationMechanismOutcome.AUTHENTICATED;
                } else {
                    securityContext.authenticationFailed(MESSAGES.authenticationFailed(token), MECHANISM_NAME);
                    //servletRequestContext.getCurrentServletContext().getSession(exchange, true);
                    outcome = AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
                }
            } finally {
                if (outcome == AuthenticationMechanismOutcome.AUTHENTICATED) {
                    //response.setHeader(JwtManager.AUTH_HEADER_KEY, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);
                    //handleRedirectBack(exchange);
                    //exchange.endExchange();
                }
                LOG.log(LEVEL, "authenticate - result outcome = " + outcome);
                request.removeAttribute(HttpServerExchange.class.getName());
                return outcome != null ? outcome : AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }
        }

        if (!securityContext.isAuthenticationRequired()) {
            // Nao e' necessario autenticar
            LOG.log(LEVEL, "authenticate - Autenticacao desnecessaria");
            return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
        }

        /* ------------------------------------------------- */

        // Entra no processo de autenticaçao normal
        AuthenticationMechanismOutcome result = AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
        try {
            result = super.authenticate(exchange, securityContext);
        } finally {
            request.removeAttribute(HttpServerExchange.class.getName());
        }

        LOG.log(LEVEL, "authenticate - result = " + result);
        return result;
    }

    @Override
    public AuthenticationMechanismOutcome runFormAuth(HttpServerExchange exchange, SecurityContext securityContext) {
        LOG.log(LEVEL, "runFormAuth(" + exchange + ", " + securityContext + ")");

        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        HttpServletRequest request = servletRequestContext.getOriginalRequest();
        LOG.log(LEVEL, "authenticate - request=" + request);

        AuthenticationMechanismOutcome result = super.runFormAuth(exchange, securityContext);
        LOG.log(LEVEL, "runFormAuth - result = " + result);

        return result;
    }

    @Override
    public ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
        LOG.log(LEVEL, "sendChallenge(" + exchange + ", " + securityContext + ")");
        ChallengeResult result = super.sendChallenge(exchange, securityContext);
        LOG.log(LEVEL, "sendChallenge = " + result);
        return result;
    }
}
