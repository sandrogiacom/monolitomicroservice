package com.monolitomicroservice.teste.common.loginmodule.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
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
import javax.sql.DataSource;

import com.monolitomicroservice.teste.common.loginmodule.AbstractLoginModule;

public class CustomDatabaseLoginModule extends AbstractLoginModule {
    private static final Logger log = Logger.getLogger(CustomDatabaseLoginModule.class.getName());

    protected String dsJndiName;
    protected String principalsQuery = "SELECT password FROM common_user WHERE login = ?";
    protected String rolesQuery = "SELECT role_code, 'Roles' FROM common_user_role WHERE login = ?";

    private List<String> roles;
    private String principal;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);

        this.dsJndiName = options.get("dsJndiName").toString();
        if (options.get("principalsQuery") != null) {
            principalsQuery = options.get("principalsQuery").toString();
        }
        if (options.get("rolesQuery") != null) {
            rolesQuery = options.get("rolesQuery").toString();
        }
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
                    this.principal = username;
                    this.roles = new LinkedList<>();

                    try (PreparedStatement ps = conn.prepareStatement(rolesQuery)) {
                        ps.setString(1, username);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                this.roles.add(rs.getString(1));
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
    protected List<String> getRoles() {
        return this.roles;
    }

    @Override
    protected String getIdentity() {
        return this.principal;
    }
}
