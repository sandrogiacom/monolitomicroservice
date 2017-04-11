package com.monolitomicroservice.teste.common.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "content")
public class RestResult implements Serializable {
    private Long elapsedTime;
    private String sessionID;
    private String container;
    private String serverContainer;
    private String login;
    private Object content;

    public RestResult() {
    }

    public RestResult(String sessionID) {
        this.sessionID = sessionID;
    }

    public RestResult(Long elapsedTime, String sessionID, Object result, String container) {
        this.elapsedTime = elapsedTime;
        this.sessionID = sessionID;
        this.content = result;
        this.container = container;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getServerContainer() {
        return serverContainer;
    }

    public void setServerContainer(String serverContainer) {
        this.serverContainer = serverContainer;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RestResult{" +
                "elapsedTime=" + elapsedTime +
                ", sessionID=" + sessionID +
                ", container=" + container +
                ", serverContainer=" + serverContainer +
                ", login=" + login +
                ", content=" + content +
                '}';
    }
}
