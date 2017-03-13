package com.monolitomicroservice.teste.performance.service;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.monolitomicroservice.teste.performancerest.persistence.TSTUser;

@Stateless(name = "UserService", mappedName = "service/UserService")
public class UserServiceImpl implements UserService {
    private static final Logger log = Logger.getLogger(UserService.class.getName());

    @PersistenceContext(unitName = "PerformancePU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TSTUser create(TSTUser t) throws Exception {
        if (findByCode(t.getUserCode()) != null) {
            throw new Exception("Duplicated user code: " + t.getUserCode());
        }
        if (findByLogin(t.getLogin()) != null) {
            throw new Exception("Duplicated login: " + t.getLogin());
        }

        em.persist(t);
        log.fine("==== User created: " + t);
        return t;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public TSTUser findByCode(String userCode) {
        TypedQuery<TSTUser> q = em.createNamedQuery("TSTUser.findByCode", TSTUser.class);
        q.setParameter("userCode", userCode);
        try {
            return q.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public TSTUser findByLogin(String login) {
        TypedQuery<TSTUser> q = em.createNamedQuery("TSTUser.findByLogin", TSTUser.class);
        q.setParameter("login", login);
        try {
            return q.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<TSTUser> find(int start, int size) {
        TypedQuery<TSTUser> q = em.createNamedQuery("TSTUser.findByRange", TSTUser.class);
        q.setFirstResult(start);
        q.setMaxResults(size);
        List<TSTUser> r = q.getResultList();
        log.fine("==== Users found: " + r.size());
        return r;
    }
}
