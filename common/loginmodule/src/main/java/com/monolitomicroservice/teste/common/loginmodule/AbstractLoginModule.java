package com.monolitomicroservice.teste.common.loginmodule;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public abstract class AbstractLoginModule implements LoginModule {
    protected final Logger log;

    protected CallbackHandler callbackHandler = null;
    protected boolean authenticated = false;
    protected Subject subject;
    protected Map sharedState;
    protected Map<String, ?> options;
    protected boolean committed = false;

    public AbstractLoginModule() {
        log = Logger.getLogger(getClass().getName());
    }

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
    public boolean commit() throws LoginException {
        log.info("BEGIN - commit");
        if (!authenticated) {
            return false;
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
                log.info("commit - Criando Principal: " + identity);
                principal = new CustomPrincipal(identity);
                subject.getPrincipals().add(principal);
            }

            CustomGroup roles = null;
            for (Principal p : subject.getPrincipals()) {
                if (p.getName().equals("Roles"))
                    break;
            }
            if (roles == null) {
                log.info("commit - Criando grupo Roles");
                roles = new CustomGroup("Roles");
                subject.getPrincipals().add(roles);
            } else {
                log.info("commit - Roles ja existe");
            }

            for (String r : getRoles()) {
                CustomGroup role = new CustomGroup(r);
                if (!roles.isMember(role)) {
                    log.info("commit - Adicionando role: " + role.getName());
                    roles.addMember(role);
                }
            }
            committed = true;
        }
        log.info("END - commit");
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

    protected abstract List<String> getRoles();

    protected abstract String getIdentity();
}
