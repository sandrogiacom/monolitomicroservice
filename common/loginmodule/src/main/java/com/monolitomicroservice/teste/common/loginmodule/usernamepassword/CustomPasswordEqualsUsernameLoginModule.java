package com.monolitomicroservice.teste.common.loginmodule.usernamepassword;

import java.security.Principal;
import java.util.Map;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.monolitomicroservice.teste.common.loginmodule.CustomGroup;
import com.monolitomicroservice.teste.common.loginmodule.CustomPrincipal;

public class CustomPasswordEqualsUsernameLoginModule implements LoginModule {
    private static final Logger log = Logger.getLogger(CustomPasswordEqualsUsernameLoginModule.class.getName());

    private CallbackHandler callbackHandler = null;
    private boolean authenticated = false;
    private Subject subject;
    private Map sharedState;
    private Map<String, ?> options;
    private boolean committed = false;

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
        boolean result = true;

        Callback nameCBack = new NameCallback("Username: ");
        Callback pwdCBack = new PasswordCallback("Password: ", false);

        Callback[] callbacks = new Callback[2];
        callbacks[0] = nameCBack;
        callbacks[1] = pwdCBack;

        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }

        String username = ((NameCallback) callbacks[0]).getName();
        String password = new String(((PasswordCallback) callbacks[1]).getPassword());

        log.info("login - Username entered by user: " + username);
        log.info("login - Password entered by user: " + password.toString());

        if (username.equals(password)) {
            log.info("login - Credentials verified!!");

            Principal principal = new CustomPrincipal(username);
            subject.getPrincipals().add(principal);
            CustomGroup roles = new CustomGroup("Roles");
            subject.getPrincipals().add(roles);
            CustomGroup user = new CustomGroup("user");
            roles.addMember(user);
            if (username.equals("admin")) {
                CustomGroup admin = new CustomGroup("admin");
                roles.addMember(admin);
            }
            this.sharedState.put("j_username", username);
            this.sharedState.put("j_password", password);
            this.sharedState.put("javax.security.auth.login.name", username);
            this.sharedState.put("javax.security.auth.login.password", password);
            this.sharedState.put("_logged_", true);
        } else {
            result = false;
        }
        authenticated = result;
        log.info("END - login - result=" + result);

        return result;
    }

    @Override
    public boolean commit() throws LoginException {
        log.info("commit");
        if (!authenticated) {
            return false;
        } else {
            committed = true;
        }
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        log.info("abort");
        authenticated = false;
        committed = false;
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        log.info("logout");
        authenticated = false;
        committed = false;
        return false;
    }
}
