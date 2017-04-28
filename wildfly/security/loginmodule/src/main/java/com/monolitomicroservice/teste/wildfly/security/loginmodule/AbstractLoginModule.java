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

public abstract class AbstractLoginModule implements LoginModule {
    protected final Logger LOG;
    protected static Level LEVEL = Level.FINEST;

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
        LOG.log(LEVEL, "BEGIN - initialize");
        LOG.log(LEVEL, "initialize - subject=" + subject);
        this.callbackHandler = callbackHandler;
        this.subject = subject;
        this.options = options;
        this.sharedState = sharedState;
        LOG.log(LEVEL, "END - initialize");
    }

    @Override
    public boolean commit() throws LoginException {
        LOG.log(LEVEL, "BEGIN - commit - authenticated=" + authenticated);
        if (!authenticated) {
            if (this.sharedState.get("_logged_") == null) {
                return false;
            }
        } else {
            List<String> l = getRoles();
            LOG.log(LEVEL, "%%%%%% commit - 1 - roles: " + l);

            Principal principal = null;
            String identity = getIdentity();
            LOG.log(LEVEL, "%%%%%% commit - 2 - identity: " + identity);
            for (Principal p : subject.getPrincipals()) {
                LOG.log(LEVEL, "%%%%%% commit - 2.1 - principal: " + p);
                if (p.getName().equals(identity)) {
                    principal = p;
                    break;
                }
            }
            LOG.log(LEVEL, "%%%%%% commit - 3 - principal: " + principal);
            if (principal == null) {
                LOG.log(LEVEL, "commit - Criando Principal: " + identity);
                principal = new CustomPrincipal(identity);
                subject.getPrincipals().add(principal);
            }
            LOG.log(LEVEL, "%%%%%% commit - 4 - subject: " + subject);

            CustomGroup roles = null;
            for (Principal p : subject.getPrincipals()) {
                LOG.log(LEVEL, "%%%%%% commit - 4.1 - principal: " + p);
                if (p.getName().equals("Roles")) {
                    roles = (CustomGroup) p;
                    break;
                }
            }
            LOG.log(LEVEL, "%%%%%% commit - 5 - roles: " + roles);

            if (roles == null) {
                LOG.log(LEVEL, "commit - Criando grupo Roles");
                roles = new CustomGroup("Roles");
                subject.getPrincipals().add(roles);
            } else {
                LOG.log(LEVEL, "commit - Roles ja existe");
            }
            LOG.log(LEVEL, "%%%%%% commit - 6 - subject: " + subject);
            LOG.log(LEVEL, "%%%%%% commit - 7 - getRoles: " + getRoles());

            for (String r : getRoles()) {
                CustomGroup role = new CustomGroup(r);
                if (!roles.isMember(role)) {
                    LOG.log(LEVEL, "commit - Adicionando role: " + role.getName());
                    roles.addMember(role);
                }
            }
            LOG.log(LEVEL, "%%%%%% commit - 8 - subject: " + subject);
            committed = true;
        }
        LOG.log(LEVEL, "END - commit");
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        LOG.log(LEVEL, "abort");
        authenticated = false;
        committed = false;
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        LOG.log(LEVEL, "logout");
        authenticated = false;
        committed = false;
        return false;
    }

    protected abstract List<String> getRoles();

    protected abstract String getIdentity();
}
