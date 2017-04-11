package com.monolitomicroservice.teste.common.loginmodule.usernamepassword;

import java.util.List;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;

import com.monolitomicroservice.teste.common.loginmodule.AbstractLoginModule;

public class CustomPasswordEqualsUsernameLoginModule extends AbstractLoginModule {
    private static final Logger log = Logger.getLogger(CustomPasswordEqualsUsernameLoginModule.class.getName());

    private List<String> roles;
    private String principal;

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

            this.principal = username;
            this.roles.add("user");
            if (username.equals("admin")) {
                roles.add("admin");
            }
            this.sharedState.put("j_username", username);
            this.sharedState.put("j_password", password);
            this.sharedState.put("javax.security.auth.login.name", username);
            this.sharedState.put("javax.security.auth.login.password", password);
            this.sharedState.put("_logged_", true);
        } else {
            result = false;
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
}
