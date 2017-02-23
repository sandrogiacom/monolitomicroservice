package com.monolitomicroservice.teste.performance.service;

import java.util.List;

import javax.ejb.Remote;

import com.monolitomicroservice.teste.performancerest.persistence.TSTUser;

@Remote
public interface UserService {
    TSTUser create(TSTUser t) throws Exception;

    TSTUser findByCode(String userCode);

    TSTUser findByLogin(String login);

    List<TSTUser> find(int start, int size);
}
