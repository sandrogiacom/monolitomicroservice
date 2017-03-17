package com.monolitomicroservice.teste.performance.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "result")
public class CallResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String container;
    private Object content;

    public CallResult() {
    }

    public CallResult(String container) {
        this.container = container;
    }

    public CallResult(String container, Object content) {
        this.container = container;
        this.content = content;
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
}
