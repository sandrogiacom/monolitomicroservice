package com.monolitomicroservice.teste.session.service;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

//@Stateless(name = "UserService", mappedName = "service/UserService")
@Stateless
public class UserServiceImpl implements UserService {
    private static final Logger log = Logger.getLogger(UserService.class.getSimpleName());

    @Resource
    private SessionContext context;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String getCurrentUser() {
        return context.getCallerPrincipal().getName();
    }
}
