package com.monolitomicroservice.teste.wildfly.security.mechanism;

import static io.undertow.UndertowMessages.MESSAGES;

import java.io.IOException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.monolitomicroservice.teste.wildfly.security.common.jwt.JwtManager;

import io.undertow.UndertowLogger;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import io.undertow.security.impl.FormAuthenticationMechanism;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.handlers.security.ServletFormAuthenticationMechanism;
import io.undertow.util.Headers;

public class FormCustomAuthenticationMechanism extends ServletFormAuthenticationMechanism {
    public static final String MECHANISM_NAME = "FORM";

    private static final Logger LOG = Logger.getLogger(FormCustomAuthenticationMechanism.class.getSimpleName());
    protected static Level LEVEL = Level.FINEST;

    private FormParserFactory formParserFactory;

    public FormCustomAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage) {
        super(formParserFactory, name, loginPage, errorPage, FormAuthenticationMechanism.DEFAULT_POST_LOCATION);
        this.formParserFactory = formParserFactory;
        LOG.log(LEVEL, "FormCustomAuthenticationMechanism(" + formParserFactory + ", " + name + ", " + loginPage + ", " + errorPage + ")");
    }

    @Override
    public AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
        LOG.log(LEVEL, "authenticate(" + exchange + ", " + securityContext + "): isAuthenticationRequired=" + securityContext.isAuthenticationRequired());

        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        HttpServletRequest request = servletRequestContext.getOriginalRequest();

        Principal principal = request.getUserPrincipal();
        LOG.log(LEVEL, "authenticate() - principal=" + principal);
        if (principal != null) {
            return AuthenticationMechanismOutcome.AUTHENTICATED;
        }

        String token = JwtManager.getBearerToken(request);
        if (token != null) {
            // Autentica via token
            LOG.log(LEVEL, "authenticate() - token=" + token);

            AuthenticationMechanismOutcome outcome = null;
            PasswordCredential credential = new PasswordCredential(token.toCharArray());
            try {
                IdentityManager identityManager = securityContext.getIdentityManager();
                Account account = identityManager.verify(token, credential);
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
                    exchange.getResponseHeaders().add(Headers.AUTHORIZATION, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);
                    //handleRedirectBack(exchange);
                    //exchange.endExchange();
                }
                LOG.log(LEVEL, "authenticate() - result outcome = " + outcome + " - subject=" + request.getUserPrincipal());
                return outcome != null ? outcome : AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }
        }

        if (!securityContext.isAuthenticationRequired()) {
            // Nao e' necessario autenticar
            LOG.log(LEVEL, "authenticate() - Autenticacao desnecessaria");
            return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
        }

        /* ------------------------------------------------- */

        // Entra no processo de autenticaçao normal
        AuthenticationMechanismOutcome result = AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
        result = super.authenticate(exchange, securityContext);

        LOG.log(LEVEL, "authenticate() - result = " + result + " - principal=" + request.getUserPrincipal());
        return result;
    }

    @Override
    public AuthenticationMechanismOutcome runFormAuth(HttpServerExchange exchange, SecurityContext securityContext) {
        LOG.log(LEVEL, "runFormAuth(" + exchange + ", " + securityContext + ")");

        /*
        AuthenticationMechanismOutcome result = super.runFormAuth(exchange, securityContext);
        */

        final FormDataParser parser = formParserFactory.createParser(exchange);
        if (parser == null) {
            UndertowLogger.SECURITY_LOGGER.debug("Could not authenticate as no form parser is present");
            // TODO - May need a better error signaling mechanism here to prevent repeated attempts.
            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
        }

        try {
            final FormData data = parser.parseBlocking();
            final FormData.FormValue jUsername = data.getFirst("j_username");
            final FormData.FormValue jPassword = data.getFirst("j_password");
            if (jUsername == null || jPassword == null) {
                UndertowLogger.SECURITY_LOGGER.debugf("Could not authenticate as username or password was not present in the posted result for %s", exchange);
                return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }
            final String userName = jUsername.getValue();
            final String password = jPassword.getValue();
            AuthenticationMechanismOutcome outcome = null;
            PasswordCredential credential = new PasswordCredential(password.toCharArray());
            try {
                IdentityManager identityManager = securityContext.getIdentityManager();
                Account account = identityManager.verify(userName, credential);
                if (account != null) {
                    securityContext.authenticationComplete(account, MECHANISM_NAME, true);
                    UndertowLogger.SECURITY_LOGGER.debugf("Authenticated user %s using for auth for %s", account.getPrincipal().getName(), exchange);
                    outcome = AuthenticationMechanismOutcome.AUTHENTICATED;
                } else {
                    securityContext.authenticationFailed(MESSAGES.authenticationFailed(userName), MECHANISM_NAME);
                }
            } finally {
                if (outcome == AuthenticationMechanismOutcome.AUTHENTICATED) {
                    Account account = securityContext.getAuthenticatedAccount();
                    JwtManager jwtManager = new JwtManager();
                    StringBuilder sb = new StringBuilder();
                    for (String s : account.getRoles()) {
                        if (sb.length() > 0)
                            sb.append(",");
                        sb.append(s);
                    }
                    String token = jwtManager.createToken(account.getPrincipal().getName(), sb.toString());
                    exchange.getResponseHeaders().add(Headers.AUTHORIZATION, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);

                    handleRedirectBack(exchange);
                    exchange.endExchange();
                }
                return outcome != null ? outcome : AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
        LOG.log(LEVEL, "runFormAuth() - result = " + result);

        return result;
        */
    }

    @Override
    public ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
        LOG.log(LEVEL, "sendChallenge(" + exchange + ", " + securityContext + ")");
        ChallengeResult result = super.sendChallenge(exchange, securityContext);
        LOG.log(LEVEL, "sendChallenge = " + result);
        return result;
    }
}
