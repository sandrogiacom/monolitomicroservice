package com.monolitomicroservice.teste.session.service;

import javax.ejb.Remote;

@Remote
public interface UserService {
    String getCurrentUser();
}
