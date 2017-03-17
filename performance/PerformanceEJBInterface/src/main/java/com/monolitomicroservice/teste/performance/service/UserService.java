package com.monolitomicroservice.teste.performance.service;

import javax.ejb.Remote;

import com.monolitomicroservice.teste.performance.common.CallResult;
import com.monolitomicroservice.teste.performance.common.UserVO;

@Remote
public interface UserService {
    CallResult create(UserVO t) throws Exception;

    CallResult findByCode(String userCode);

    CallResult findByLogin(String login);

    CallResult find(int start, int size);
}
