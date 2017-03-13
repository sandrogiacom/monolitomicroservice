package com.monolitomicroservice.teste.performance.service;

import javax.ejb.Remote;

import com.monolitomicroservice.teste.performancerest.persistence.TSTUser;

@Remote
public interface UserService {
    UserResult create(TSTUser t) throws Exception;

    UserResult findByCode(String userCode);

    UserResult findByLogin(String login);

    ListUserResult find(int start, int size);
}
