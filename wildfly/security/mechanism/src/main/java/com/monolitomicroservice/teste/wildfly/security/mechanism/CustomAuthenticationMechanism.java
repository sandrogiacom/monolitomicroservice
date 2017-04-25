package com.monolitomicroservice.teste.wildfly.security.mechanism;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;

public class CustomAuthenticationMechanism implements AuthenticationMechanism {
    private static final Logger LOG = Logger.getLogger(CustomAuthenticationMechanism.class.getName());

    @Override
    public AuthenticationMechanismOutcome authenticate(HttpServerExchange httpServerExchange, SecurityContext securityContext) {
        LOG.finest("&&&&&&&&&& authenticate - isAuthenticationRequired=" + securityContext.isAuthenticationRequired());

        /*
        if (!securityContext.isAuthenticationRequired()) {
            return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
        }

        ServletRequestContext servletRequestContext = httpServerExchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        HttpServletRequest request = servletRequestContext.getOriginalRequest();
        HttpServletResponse response = servletRequestContext.getOriginalResponse();

        Principal principal = request.getUserPrincipal();
        LOG.info("&&&&&&&&&& authenticate - principal=" + principal);

        if (principal != null && principal.getName() != null) {
            return AuthenticationMechanismOutcome.AUTHENTICATED;
        }

        String token = JwtManager.getBearerToken(request);
        LOG.info("&&&&&&&&&& authenticate - token=" + token);

        String username = request.getParameter("j_username");
        String password = request.getParameter("j_password");
        LOG.info("&&&&&&&&&& authenticate - username=" + username + ", password=" + password);

        if (token != null) {
            LOG.info("&&&&&&&&&& authenticate - vai logar via JWT");
            Account acc = securityContext.getIdentityManager().verify(token,
                    new PasswordCredential(token.toCharArray()));
            LOG.info("&&&&&&&&&& authenticate - acc=" + acc);

            if (acc == null) {
                securityContext.authenticationFailed("invalid token", "CUSTOMAUTH");
                servletRequestContext.getCurrentServletContext().getSession(httpServerExchange, true);
                return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }

            securityContext.authenticationComplete(acc, "CUSTOMAUTH", true);
            response.setHeader(JwtManager.AUTH_HEADER_KEY, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);
            return AuthenticationMechanismOutcome.AUTHENTICATED;
        } else if (username != null) {
            LOG.info("&&&&&&&&&& authenticate - vai logar via Username/Password");
            Account acc = securityContext.getIdentityManager().verify(username,
                    new PasswordCredential(password.toCharArray()));
            LOG.info("&&&&&&&&&& authenticate - acc=" + acc);

            if (acc == null) {
                securityContext.authenticationFailed("invalid token", "CUSTOMAUTH");
                servletRequestContext.getCurrentServletContext().getSession(httpServerExchange, true);

                token = (String)request.getAttribute("_jwt_token_");
                LOG.info("&&&&&&&&&& #### username authenticate - token=" + token);
                if (token != null) {
                    response.setHeader(JwtManager.AUTH_HEADER_KEY, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);
                }

                return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }
        }
        */

        return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }

    @Override
    public ChallengeResult sendChallenge(HttpServerExchange httpServerExchange, SecurityContext securityContext) {
        LOG.finest("@@@@@@@@@@@@@@@@@@@ sendChallenge - isAuthenticationRequired=" + securityContext.isAuthenticationRequired());
        return new ChallengeResult(true, HttpServletResponse.SC_UNAUTHORIZED);
    }
}
