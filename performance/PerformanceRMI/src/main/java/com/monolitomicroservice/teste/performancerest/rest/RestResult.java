package com.monolitomicroservice.teste.performancerest.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "content")
public class RestResult implements Serializable {
    private Long elapsedTime;
    private Object content;

    public RestResult() {
    }

    public RestResult(Long elapsedTime, Object result) {
        this.elapsedTime = elapsedTime;
        this.content = result;
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

    @Override
    public String toString() {
        return "RestResult{" +
                "elapsedTime=" + elapsedTime +
                ", content=" + content +
                '}';
    }
}
