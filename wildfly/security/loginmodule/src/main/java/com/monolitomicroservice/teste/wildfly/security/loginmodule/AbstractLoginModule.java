package com.monolitomicroservice.teste.wildfly.security.loginmodule;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.monolitomicroservice.teste.wildfly.security.common.CustomGroup;
import com.monolitomicroservice.teste.wildfly.security.common.CustomPrincipal;
import com.monolitomicroservice.teste.wildfly.security.common.SecurityConstants;

public abstract class AbstractLoginModule implements LoginModule {
    protected final Logger LOG;
    protected static Level LEVEL = Level.INFO;

    protected CallbackHandler callbackHandler = null;
    protected boolean authenticated = false;
    protected Subject subject;
    protected Map sharedState;
    protected Map<String, ?> options;
    protected boolean committed = false;

    public AbstractLoginModule() {
        LOG = Logger.getLogger(getClass().getSimpleName());
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        LOG.log(LEVEL, "initialize(subject=" + subject + ", callbackHandler=" + callbackHandler
                + ", sharedState=" + sharedState + ", options=" + options + ")");
        this.callbackHandler = callbackHandler;
        this.subject = subject;
        this.options = options;
        this.sharedState = sharedState;
        LOG.log(LEVEL, "initialize() - END");
    }

    @Override
    public boolean commit() throws LoginException {
        LOG.log(LEVEL, "commit() - BEGIN - authenticated=" + authenticated);
        if (!authenticated) {
            if (this.sharedState.get(SecurityConstants.LOGGED_ATTRIBUTE) == null) {
                return false;
            }
        } else {
            List<String> l = getRoles();

            Principal principal = null;
            String identity = getIdentity();
            for (Principal p : subject.getPrincipals()) {
                if (p.getName().equals(identity)) {
                    principal = p;
                    break;
                }
            }
            if (principal == null) {
                principal = new CustomPrincipal(identity);
                subject.getPrincipals().add(principal);
            }

            CustomGroup roles = null;
            for (Principal p : subject.getPrincipals()) {
                if (p.getName().equals("Roles")) {
                    roles = (CustomGroup) p;
                    break;
                }
            }

            if (roles == null) {
                roles = new CustomGroup("Roles");
                subject.getPrincipals().add(roles);
            }

            for (String r : getRoles()) {
                CustomGroup role = new CustomGroup(r);
                if (!roles.isMember(role)) {
                    LOG.log(LEVEL, "commit - Adicionando role: " + role.getName());
                    roles.addMember(role);
                }
            }
            LOG.log(LEVEL, "commit() - subject: " + subject);
            committed = true;
        }
        LOG.log(LEVEL, "commit() - END");
        return committed;
    }

    @Override
    public boolean abort() throws LoginException {
        LOG.log(LEVEL, "abort()");
        authenticated = false;
        committed = false;
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        LOG.log(LEVEL, "logout() - " + subject);
        authenticated = false;
        committed = false;
        return false;
    }

    protected abstract List<String> getRoles();

    protected abstract String getIdentity();
}
