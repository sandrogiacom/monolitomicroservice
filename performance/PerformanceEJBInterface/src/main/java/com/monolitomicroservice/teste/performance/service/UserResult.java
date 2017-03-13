package com.monolitomicroservice.teste.performance.service;

import java.io.Serializable;

import com.monolitomicroservice.teste.performancerest.persistence.TSTUser;

public class UserResult implements Serializable {
    private String container;
    private TSTUser user;

    public UserResult() {
    }

    public UserResult(String container, TSTUser user) {
        this.container = container;
        this.user = user;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public TSTUser getUser() {
        return user;
    }

    public void setUser(TSTUser user) {
        this.user = user;
    }
}
