package com.monolitomicroservice.teste.performance.service;

import java.io.Serializable;
import java.util.List;

import com.monolitomicroservice.teste.performancerest.persistence.TSTUser;

public class ListUserResult implements Serializable {
    private String container;
    private List<TSTUser> users;

    public ListUserResult() {
    }

    public ListUserResult(String container, List<TSTUser> users) {
        this.container = container;
        this.users = users;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public List<TSTUser> getUsers() {
        return users;
    }

    public void setUsers(List<TSTUser> users) {
        this.users = users;
    }
}
