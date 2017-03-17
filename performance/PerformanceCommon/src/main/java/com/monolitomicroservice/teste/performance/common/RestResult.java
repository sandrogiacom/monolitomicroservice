package com.monolitomicroservice.teste.performance.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "content")
public class RestResult implements Serializable {
    private Long elapsedTime;
    private String container;
    private String serverContainer;
    private Object content;

    public RestResult() {
    }

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
                ", container=" + container +
                ", serverContainer=" + serverContainer +
                ", content=" + content +
                '}';
    }
}
