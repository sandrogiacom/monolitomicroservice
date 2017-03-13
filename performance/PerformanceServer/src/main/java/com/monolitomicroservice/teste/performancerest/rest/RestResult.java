package com.monolitomicroservice.teste.performancerest.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "content")
public class RestResult implements Serializable {
    private Long elapsedTime;
    private String container;
    private Object content;

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
                ", content=" + content +
                '}';
    }
}
