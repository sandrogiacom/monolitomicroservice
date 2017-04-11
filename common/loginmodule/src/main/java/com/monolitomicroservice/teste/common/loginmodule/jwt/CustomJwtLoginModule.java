package com.monolitomicroservice.teste.common.loginmodule.jwt;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.monolitomicroservice.teste.common.loginmodule.CustomGroup;
import com.monolitomicroservice.teste.common.loginmodule.CustomPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class CustomJwtLoginModule implements LoginModule {
    private static final Logger log = Logger.getLogger(CustomJwtLoginModule.class.getName());

    private CallbackHandler callbackHandler = null;
    private Subject subject;
    private Map sharedState;
    private Map<String, ?> options;
    //private boolean committed = false;
    //private boolean authenticated = false;

    private Principal identity;
    private Principal group;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        log.info("BEGIN - initialize");
        log.info("initialize - subject=" + subject);
        this.callbackHandler = callbackHandler;
        this.subject = subject;
        this.options = options;
        this.sharedState = sharedState;
        log.info("END - initialize");
    }

    @Override
    public boolean login() throws LoginException {
        log.info("BEGIN - login");
        log.info("login - _logged_=" + this.sharedState.get("_logged_"));
        if (this.sharedState.get("_logged_") != null && this.sharedState.get("_logged_").toString().equalsIgnoreCase("true")) {
            log.info("login - abortando checagem");
            return true;
        }
        boolean result = false;
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
                identity = new CustomPrincipal(user);
                log.info("login - JWT provided - 5 - " + identity);

                String role = (String) jws.getBody().get("role");
                log.info("login - JWT provided - 6 - " + role);
                group = new CustomPrincipal(role);
                log.info("login - JWT provided - 7 - " + group);

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
    public boolean commit() throws LoginException {
        Set<Principal> principals = subject.getPrincipals();
        principals.add(identity);

        CustomGroup roles = new CustomGroup("Roles");
        roles.addMember(group);
        principals.add(roles);

        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        log.info("abort");
        identity = null;
        group = null;
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        log.info("logout");
        identity = null;
        group = null;
        return true;
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
