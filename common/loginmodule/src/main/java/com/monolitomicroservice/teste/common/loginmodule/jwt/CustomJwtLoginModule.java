package com.monolitomicroservice.teste.common.loginmodule.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.monolitomicroservice.teste.common.loginmodule.AbstractLoginModule;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class CustomJwtLoginModule extends AbstractLoginModule {
    private static final Logger log = Logger.getLogger(CustomJwtLoginModule.class.getName());

    private List<String> roles;
    private String principal;

    @Override
    public boolean login() throws LoginException {
        log.info("BEGIN - login");
        log.info("login - _logged_=" + this.sharedState.get("_logged_"));

        HttpServletRequest request = null;
        HttpServletResponse response = null;

        try {
            log.info("#### ::: login - 1");
            request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
            log.info("::: login - 2::: request=" + request);
            response = (HttpServletResponse) PolicyContext.getContext("javax.servlet.http.HttpServletResponse");
            log.info("::: login - 3::: response=" + response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.info("#### ::: login - 4");

        boolean result = false;

        if (this.sharedState.get("_logged_") != null && this.sharedState.get("_logged_").toString().equalsIgnoreCase("true")) {
            log.info("login - abortando checagem");
            result = true;
        }
        String jwt = getJwt();

        if (jwt != null) {
            try {
                log.info("login - JWT provided - 1");

                JwtManager jwtManager = lookupJwtManager();
                log.info("login - JWT provided - 2");

                // verify the received token
                Jws<Claims> jws = jwtManager.parseToken(jwt);
                log.info("login - JWT provided - 3");

                // now we can trust its information...
                String user = jws.getBody().getSubject();
                log.info("login - JWT provided - 4 - " + user);
                principal = user;
                //identity = new CustomPrincipal(user);
                log.info("login - JWT provided - 5 - " + principal);

                String role = (String) jws.getBody().get("role");
                log.info("login - JWT provided - 6 - " + role);
                roles = new ArrayList<>();
                StringTokenizer st = new StringTokenizer(role, ",");
                while (st.hasMoreTokens()) {
                    roles.add(st.nextToken());
                }
                //group = new CustomPrincipal(role);
                log.info("login - JWT provided - 7 - " + roles);

                log.info("login - JWT is valid, logging in user " + user + " with role " + role);

                result = true;
            } catch (SignatureException | MalformedJwtException
                    | UnsupportedJwtException | IllegalArgumentException e) {
                log.info("login - ERROR: Invalid security token provided");
                throw new FailedLoginException("Invalid security token provided");
            } catch (ExpiredJwtException e) {
                log.info("login - ERROR: The security token is expired");
                throw new CredentialExpiredException("The security token is expired");
            }
        }
        log.info("END - login - result=" + result);
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
            log.log(Level.SEVERE, msg, e);
            throw new LoginException(msg);
        }
    }

    private JwtManager lookupJwtManager() {
        try {
            BeanManager beanManager = InitialContext.doLookup("java:comp/BeanManager");
            Set<Bean<?>> beans = beanManager.getBeans(JwtManager.class);
            if (beans.isEmpty()) {
                throw new RuntimeException("Failed looking up CDI Bean " + JwtManager.class.getName()
                        + ": Found " + beans.size() + " ");
            }
            Bean<?> bean = beans.iterator().next();
            CreationalContext<?> context = beanManager.createCreationalContext(bean);
            return (JwtManager) beanManager.getReference(bean, JwtManager.class, context);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
