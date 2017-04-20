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
        /*
        LOG.info("&&&&&&&&&& authenticate - isAuthenticationRequired=" + securityContext.isAuthenticationRequired());

        ServletRequestContext servletRequestContext = httpServerExchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);

        HttpServletRequest request = servletRequestContext.getOriginalRequest();
        LOG.info("&&&&&&&&&& authenticate - request=" + request);

        Principal principal = request.getUserPrincipal();
        LOG.info("&&&&&&&&&& authenticate - principal=" + principal);

        HttpServletResponse response = servletRequestContext.getOriginalResponse();

        String token = JwtManager.getBearerToken(request);
        LOG.info("&&&&&&&&&& authenticate - token=" + token);

        String username = request.getParameter("j_username");
        String password = request.getParameter("j_password");
        LOG.info("&&&&&&&&&& authenticate - username=" + username + ", password=" + password);
        */

        /*
        if (token != null) {
            LOG.info("&&&&&&&&&& #### token authenticate - vai fazer login");
            if (securityContext.login(token, token)) {
                LOG.info("&&&&&&&&&& #### token authenticate - fez login");
                response.setHeader(JwtManager.AUTH_HEADER_KEY, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);
                principal = request.getUserPrincipal();
                LOG.info("&&&&&&&&&& #### token authenticate - principal=" + principal);
                return AuthenticationMechanismOutcome.AUTHENTICATED;
            }
            LOG.info("&&&&&&&&&& #### token authenticate - login FALHOU");
        } else if (username != null) {
            LOG.info("&&&&&&&&&& #### username authenticate - vai fazer login");
            if (securityContext.login(username, password)) {
                LOG.info("&&&&&&&&&& #### username authenticate - fez login");
                principal = request.getUserPrincipal();
                LOG.info("&&&&&&&&&& #### username authenticate - principal=" + principal);
                token = (String)request.getAttribute("_jwt_token_");
                LOG.info("&&&&&&&&&& #### username authenticate - token=" + token);
                if (token != null) {
                    response.setHeader(JwtManager.AUTH_HEADER_KEY, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);
                }
                return AuthenticationMechanismOutcome.AUTHENTICATED;
            }
        }
        */

        /*
        if (principal != null && principal.getName() != null) {
            return AuthenticationMechanismOutcome.AUTHENTICATED;
        }
        */

        return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }

    @Override
    public ChallengeResult sendChallenge(HttpServerExchange httpServerExchange, SecurityContext securityContext) {
        return new ChallengeResult(true, HttpServletResponse.SC_UNAUTHORIZED);
    }
}
