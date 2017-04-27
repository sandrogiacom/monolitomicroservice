package com.monolitomicroservice.teste.wildfly.security.loginmodule.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import com.monolitomicroservice.teste.wildfly.security.loginmodule.AbstractLoginModule;

public class CustomDatabaseLoginModule extends AbstractLoginModule {
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
        LOG.log(LEVEL, "BEGIN - login");
        boolean result = true;
        boolean executeLogin = true;

        try {
            HttpServletRequest request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
            if (request != null) {
                String loginType = (String) request.getAttribute("com.teste.monolitomicroservice.extension.custom");
                if (loginType != null && loginType.equals("JWT")) {
                    LOG.log(LEVEL, "::::::: logando via JWT");
                    executeLogin = false;
                    result = false;
                }
            }
        } catch (PolicyContextException e) {
            e.printStackTrace();
        }

        if (executeLogin) {
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

            LOG.log(LEVEL, "login - Username entered by user: " + username);
            LOG.log(LEVEL, "login - Password entered by user: " + password.toString());

            try {
                DataSource ds = (DataSource) new InitialContext().lookup(dsJndiName);
                try (Connection conn = ds.getConnection()) {
                    try (PreparedStatement ps = conn.prepareStatement(principalsQuery)) {
                        ps.setString(1, username);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                LOG.log(LEVEL, "login - Username exists: " + username);
                                String s = rs.getString(1);
                                if (password.equals(s)) {
                                    LOG.log(LEVEL, "login - Password OK");
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
                        LOG.log(LEVEL, "login - Credentials verified!!");
                        this.principal = username;
                        this.roles = new LinkedList<>();

                        StringBuilder sbRoles = new StringBuilder();
                        try (PreparedStatement ps = conn.prepareStatement(rolesQuery)) {
                            ps.setString(1, username);
                            try (ResultSet rs = ps.executeQuery()) {
                                while (rs.next()) {
                                    String role = rs.getString(1);
                                    this.roles.add(role);
                                    if (sbRoles.length() > 0)
                                        sbRoles.append(",");
                                    sbRoles.append(role);
                                }
                            }
                        }

                        this.sharedState.put("j_username", username);
                        this.sharedState.put("j_password", password);
                        this.sharedState.put("javax.security.auth.login.name", username);
                        this.sharedState.put("javax.security.auth.login.password", password);
                        this.sharedState.put("_logged_", "true");
                        this.sharedState.put("Roles", sbRoles.toString());
                    }
                }
            } catch (NamingException | SQLException e) {
                e.printStackTrace();
                result = false;
                throw new FailedLoginException(e.getMessage());
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
}
