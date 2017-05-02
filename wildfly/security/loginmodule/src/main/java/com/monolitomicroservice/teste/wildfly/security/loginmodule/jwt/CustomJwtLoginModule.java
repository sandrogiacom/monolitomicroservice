package com.monolitomicroservice.teste.wildfly.security.loginmodule.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpServletRequest;

import com.monolitomicroservice.teste.wildfly.security.common.SecurityConstants;
import com.monolitomicroservice.teste.wildfly.security.common.jwt.JwtManager;
import com.monolitomicroservice.teste.wildfly.security.loginmodule.AbstractLoginModule;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class CustomJwtLoginModule extends AbstractLoginModule {
    private List<String> roles = new ArrayList<>();
    private String principal;

    private static JwtManager jwtManager = new JwtManager();

    @Override
    public boolean login() throws LoginException {
        LOG.log(LEVEL, "login() - BEGIN");
        boolean logged = this.sharedState.get(SecurityConstants.LOGGED_ATTRIBUTE) != null && this.sharedState.get(SecurityConstants.LOGGED_ATTRIBUTE).toString().equals(Boolean.TRUE.toString());
        LOG.log(LEVEL, "login() - logged=" + logged);

        HttpServletRequest request = null;
        String username = (String) this.sharedState.get("javax.security.auth.login.name");
        boolean result = false;
        String jwt = getJwt();

        if (jwt != null) {
            LOG.log(LEVEL, "login() - JWT provided: jwt=" + jwt);
            try {
                request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
                HttpServerExchange exchange = (HttpServerExchange) request.getAttribute(HttpServerExchange.class.getName());

                if (logged && jwt.equals(username)) {
                    LOG.log(LEVEL, "login() - Ja logado, vai criar o token -> Roles=" + this.sharedState.get("Roles"));
                    principal = username;
                    String token = jwtManager.createToken(username, this.sharedState.get("Roles") != null ? (String) this.sharedState.get("Roles") : "user");
                    this.sharedState.put(SecurityConstants.JWT_ATTRIBUTE, token);
                    if (request != null) {
                        request.setAttribute(SecurityConstants.JWT_ATTRIBUTE, token);
                    }
                    if (exchange != null) {
                        exchange.getResponseHeaders().add(Headers.AUTHORIZATION, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);
                    }
                } else {
                    // verify the received token
                    Jws<Claims> jws = jwtManager.parseToken(jwt);
                    LOG.log(LEVEL, "login() - Logando via token: " + jws);

                    // now we can trust its information...
                    String user = jws.getBody().getSubject();
                    principal = user;

                    String role = (String) jws.getBody().get("role");
                    StringTokenizer st = new StringTokenizer(role, ",");
                    while (st.hasMoreTokens()) {
                        roles.add(st.nextToken());
                    }

                    this.sharedState.put("j_username", username);
                    this.sharedState.put("j_password", username);
                    this.sharedState.put("javax.security.auth.login.name", username);
                    this.sharedState.put("javax.security.auth.login.password", username);
                    this.sharedState.put(SecurityConstants.LOGGED_ATTRIBUTE, Boolean.TRUE.toString());
                    if (request != null) {
                        request.setAttribute(SecurityConstants.JWT_ATTRIBUTE, jwt);
                    }
                    if (exchange != null) {
                        exchange.getResponseHeaders().add(Headers.AUTHORIZATION, JwtManager.AUTH_HEADER_VALUE_PREFIX + jwt);
                        request.setAttribute(SecurityConstants.LOGOUT_REQUIRED_ATTRIBUTE, Boolean.TRUE.toString());
                    }
                }

                result = true;
            } catch (SignatureException | MalformedJwtException
                    | UnsupportedJwtException | IllegalArgumentException e) {
                e.printStackTrace();
                LOG.log(LEVEL, "login - ERROR: Invalid security token provided");
                throw new FailedLoginException("Invalid security token provided");
            } catch (ExpiredJwtException e) {
                e.printStackTrace();
                LOG.log(LEVEL, "login - ERROR: The security token is expired");
                throw new CredentialExpiredException("The security token is expired");
            } catch (Exception ex) {
                ex.printStackTrace();
                LOG.log(LEVEL, "login - ERROR: Unknown error");
                throw new FailedLoginException("Unknown error");
            }
        }
        authenticated = result;
        LOG.log(LEVEL, "login() - END - result=" + result);

        return result;
    }

    @Override
    protected List<String> getRoles() {
        return this.roles;
    }

    @Override
    protected String getIdentity() {
        return this.principal;
    }

    private String getJwt() throws LoginException {
        NameCallback callback = new NameCallback("prompt");
        try {
            callbackHandler.handle(new Callback[]{callback});
            return callback.getName();
        } catch (IOException | UnsupportedCallbackException e) {
            String msg = "Failed getting the security token";
            LOG.log(Level.SEVERE, msg, e);
            throw new LoginException(msg);
        }
    }
}
