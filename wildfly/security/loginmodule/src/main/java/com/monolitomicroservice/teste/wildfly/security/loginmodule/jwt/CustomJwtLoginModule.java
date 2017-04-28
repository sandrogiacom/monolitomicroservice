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
        LOG.log(LEVEL, "BEGIN - login");
        boolean logged = this.sharedState.get("_logged_") != null && this.sharedState.get("_logged_").toString().equals("true");
        LOG.log(LEVEL, "login - _logged_=" + logged);

        HttpServletRequest request = null;
        String username = (String) this.sharedState.get("javax.security.auth.login.name");
        boolean result = false;
        String jwt = getJwt();

        if (jwt != null) {
            try {
                request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
                LOG.log(LEVEL, "login - JWT provided - 1: jwt=" + jwt);

                HttpServerExchange exchange = (HttpServerExchange) request.getAttribute(HttpServerExchange.class.getName());
                LOG.log(LEVEL, "login - JWT provided - 2: exchange=" + exchange);

                if (logged && jwt.equals(username)) {
                    LOG.log(LEVEL, "login - JWT provided - 3.1: Ja logado, vai criar o token -> Roles=" + this.sharedState.get("Roles"));
                    principal = username;
                    String token = jwtManager.createToken(username, this.sharedState.get("Roles") != null ? (String) this.sharedState.get("Roles") : "user");
                    LOG.log(LEVEL, "login - JWT provided - 3.2: token=" + token);
                    LOG.log(LEVEL, "login - JWT provided - 3.3: subject=" + subject);
                    this.sharedState.put("_jwt_token_", token);
                    if (request != null) {
                        request.setAttribute("_jwt_token_", token);
                    }
                    if (exchange != null) {
                        exchange.getResponseHeaders().add(Headers.AUTHORIZATION, JwtManager.AUTH_HEADER_VALUE_PREFIX + token);
                    }
                } else {
                    // verify the received token
                    Jws<Claims> jws = jwtManager.parseToken(jwt);
                    LOG.log(LEVEL, "login - JWT provided - 4.1: " + jws);

                    // now we can trust its information...
                    String user = jws.getBody().getSubject();
                    LOG.log(LEVEL, "login - JWT provided - 4.2 - " + user);
                    principal = user;
                    //identity = new CustomPrincipal(user);
                    LOG.log(LEVEL, "login - JWT provided - 4.3 - " + principal);

                    String role = (String) jws.getBody().get("role");
                    LOG.log(LEVEL, "login - JWT provided - 4.4 - " + role);
                    StringTokenizer st = new StringTokenizer(role, ",");
                    while (st.hasMoreTokens()) {
                        roles.add(st.nextToken());
                    }
                    //group = new CustomPrincipal(role);
                    LOG.log(LEVEL, "login - JWT provided - 4.5 - " + roles);

                    LOG.log(LEVEL, "login - JWT is valid, logging in user " + user + " with role " + role);

                    this.sharedState.put("j_username", username);
                    this.sharedState.put("j_password", username);
                    this.sharedState.put("javax.security.auth.login.name", username);
                    this.sharedState.put("javax.security.auth.login.password", username);
                    this.sharedState.put("_logged_", "true");
                    if (request != null) {
                        request.setAttribute("_jwt_token_", jwt);
                    }
                    if (exchange != null) {
                        exchange.getResponseHeaders().add(Headers.AUTHORIZATION, JwtManager.AUTH_HEADER_VALUE_PREFIX + jwt);
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
        LOG.log(LEVEL, "END - login - result=" + result);

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
