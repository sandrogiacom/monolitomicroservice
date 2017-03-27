package com.monolitomicroservice.teste.session.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.monolitomicroservice.teste.performance.common.CallResult;
import com.monolitomicroservice.teste.performance.common.UserVO;
import com.monolitomicroservice.teste.performancerest.persistence.TSTUser;

@Stateless(name = "UserService", mappedName = "service/UserService")
public class UserServiceImpl implements UserService {
    private static final Logger log = Logger.getLogger(UserService.class.getName());

    @PersistenceContext(unitName = "PerformancePU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CallResult create(UserVO t) throws Exception {
        if (findByCode(t.getUserCode()).getContent() != null) {
            throw new Exception("Duplicated user code: " + t.getUserCode());
        }
        if (findByLogin(t.getLogin()).getContent() != null) {
            throw new Exception("Duplicated login: " + t.getLogin());
        }

        TSTUser u = new TSTUser(t.getId(), t.getTenantId(), t.getUserCode(), t.getLogin(), t.getPassword(),
                t.getEmail(), t.getFirstName(), t.getLastName(), t.getFullName(), t.getBirthDate());
        em.persist(u);
        log.fine("==== User created: " + t);
        return new CallResult(System.getProperty("jboss.qualified.host.name"),
                new UserVO(u.getId(), u.getTenantId(), t.getUserCode(), t.getLogin(), t.getPassword(),
                        t.getEmail(), t.getFirstName(), t.getLastName(), t.getFullName(), t.getBirthDate()));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CallResult findByCode(String userCode) {
        TypedQuery<TSTUser> q = em.createNamedQuery("TSTUser.findByCode", TSTUser.class);
        q.setParameter("userCode", userCode);
        try {
            TSTUser t = q.getSingleResult();
            return new CallResult(System.getProperty("jboss.qualified.host.name"), new UserVO(t.getId(),
                    t.getTenantId(), t.getUserCode(), t.getLogin(), t.getPassword(),
                    t.getEmail(), t.getFirstName(), t.getLastName(), t.getFullName(), t.getBirthDate()));
        } catch (Exception ex) {
            return new CallResult(System.getProperty("jboss.qualified.host.name"), null);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CallResult findByLogin(String login) {
        TypedQuery<TSTUser> q = em.createNamedQuery("TSTUser.findByLogin", TSTUser.class);
        q.setParameter("login", login);
        try {
            TSTUser t = q.getSingleResult();
            return new CallResult(System.getProperty("jboss.qualified.host.name"), new UserVO(t.getId(),
                    t.getTenantId(), t.getUserCode(), t.getLogin(), t.getPassword(),
                    t.getEmail(), t.getFirstName(), t.getLastName(), t.getFullName(), t.getBirthDate()));
        } catch (Exception ex) {
            return new CallResult(System.getProperty("jboss.qualified.host.name"), null);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CallResult find(int start, int size) {
        TypedQuery<TSTUser> q = em.createNamedQuery("TSTUser.findByRange", TSTUser.class);
        q.setFirstResult(start);
        q.setMaxResults(size);
        List<TSTUser> r = q.getResultList();
        List<UserVO> l = new ArrayList<>(r.size());
        for (TSTUser t : r) {
            l.add(new UserVO(t.getId(),
                    t.getTenantId(), t.getUserCode(), t.getLogin(), t.getPassword(),
                    t.getEmail(), t.getFirstName(), t.getLastName(), t.getFullName(), t.getBirthDate()));
        }
        log.fine("==== Users found: " + r.size());
        return new CallResult(System.getProperty("jboss.qualified.host.name"), l);
    }
}
