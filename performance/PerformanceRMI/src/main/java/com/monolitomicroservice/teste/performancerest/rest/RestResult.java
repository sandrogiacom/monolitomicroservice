package com.monolitomicroservice.teste.performancerest.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "content")
public class RestResult implements Serializable {
    private Long elapsedTime;
    private Object content;
    private String container;
    private String serverContainer;

    public RestResult(Long elapsedTime, Object result, String container) {
        this.elapsedTime = elapsedTime;
        this.content = result;
        this.container = container;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
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

    @Override
    public String toString() {
        return "RestResult{" +
                "elapsedTime=" + elapsedTime +
                ", container=" + container +
                ", serverContainer=" + serverContainer +
                ", content=" + content +
                '}';
    }
}
