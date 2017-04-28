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
        HttpServletResponse response = servletRequestContext.getOriginalResponse();

        Principal principal = request.getUserPrincipal();
        LOG.log(LEVEL, "authenticate - principal=" + principal);

        if (principal != null && principal.getName() != null) {
            return AuthenticationMechanismOutcome.AUTHENTICATED;
        }

        //Testes
        /*
        final FormDataParser parser = formParserFactory.createParser(httpServerExchange);
        LOG.info(":::::::::::: parser=" + parser);

        if (parser != null) {
            final FormData data;
            try {
                data = parser.parseBlocking();
                LOG.info(":::::::::::: data=" + data);
                final FormData.FormValue jUsername = data.getFirst("j_username");
                LOG.info(":::::::::::: jUsername=" + jUsername.getValue());
                final FormData.FormValue jPassword = data.getFirst("j_password");
                LOG.info(":::::::::::: jPassword=" + jPassword.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */

        /*
        String token = JwtManager.getBearerToken(request);
        if (token != null) {
            LOG.log(LEVEL, "&&&&&&&&&& authenticate - token=" + token);
            LOG.log(LEVEL, "&&&&&&&&&& authenticate - vai logar via JWT");
            Account acc = securityContext.getIdentityManager().verify(token,
                    new PasswordCredential(token.toCharArray()));
            LOG.log(LEVEL, "&&&&&&&&&& authenticate - acc=" + acc);

            if (acc == null) {
                securityContext.authenticationFailed("invalid token", "CUSTOMAUTH");
                servletRequestContext.getCurrentServletContext().getSession(httpServerExchange, true);
                return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }

            securityContext.authenticationComplete(acc, "CUSTOMAUTH", true);
            response.setHeader(JwtManager.AUTH_HEADER_KEY, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);
            return AuthenticationMechanismOutcome.AUTHENTICATED;
        }
        */

        /*
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
        LOG.log(LEVEL, "@@@@@@@@@@@@@@@@@@@ sendChallenge - isAuthenticationRequired=" + securityContext.isAuthenticationRequired());
        return new ChallengeResult(true, HttpServletResponse.SC_UNAUTHORIZED);
    }
}
