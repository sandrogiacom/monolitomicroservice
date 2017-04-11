package com.monolitomicroservice.teste.common.loginmodule.database;

import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.sql.DataSource;

import com.monolitomicroservice.teste.common.loginmodule.CustomGroup;
import com.monolitomicroservice.teste.common.loginmodule.CustomPrincipal;

public class CustomDatabaseLoginModule implements LoginModule {
    private static final Logger log = Logger.getLogger(CustomDatabaseLoginModule.class.getName());

    private CallbackHandler callbackHandler = null;
    private boolean authenticated = false;
    private Subject subject;
    private Map sharedState;
    private Map<String, ?> options;
    private boolean committed = false;

    protected String dsJndiName;
    protected String principalsQuery = "SELECT password FROM common_user WHERE login = ?";
    protected String rolesQuery = "SELECT role_code, 'Roles' FROM common_user_role WHERE login = ?";

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        log.info("BEGIN - initialize");
        log.info("initialize - subject=" + subject);
        this.callbackHandler = callbackHandler;
        this.subject = subject;
        this.options = options;
        this.sharedState = sharedState;

        this.dsJndiName = options.get("dsJndiName").toString();
        if (options.get("principalsQuery") != null) {
            principalsQuery = options.get("principalsQuery").toString();
        }
        if (options.get("rolesQuery") != null) {
            rolesQuery = options.get("rolesQuery").toString();
        }

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

        try {
            DataSource ds = (DataSource) new InitialContext().lookup(dsJndiName);
            try (Connection conn = ds.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement(principalsQuery)) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            log.info("login - Username exists: " + username);
                            String s = rs.getString(1);
                            if (password.equals(s)) {
                                log.info("login - Password OK");
                            } else {
                                result = false;
                                throw new FailedLoginException("Invalid user: " + username);
                            }
                        } else {
                            result = false;
                            throw new FailedLoginException("Invalid password");
                        }
                    }
                }
                if (result) {
                    log.info("login - Credentials verified!!");
                    Principal principal = new CustomPrincipal(username);
                    subject.getPrincipals().add(principal);
                    CustomGroup roles = new CustomGroup("Roles");
                    subject.getPrincipals().add(roles);

                    try (PreparedStatement ps = conn.prepareStatement(rolesQuery)) {
                        ps.setString(1, username);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                CustomGroup role = new CustomGroup(rs.getString(1));
                                roles.addMember(role);
                            }
                        }
                    }

                    this.sharedState.put("j_username", username);
                    this.sharedState.put("j_password", password);
                    this.sharedState.put("javax.security.auth.login.name", username);
                    this.sharedState.put("javax.security.auth.login.password", password);
                    this.sharedState.put("_logged_", true);
                }
            }
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
            result = false;
            throw new FailedLoginException(e.getMessage());
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
