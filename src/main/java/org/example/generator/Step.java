package org.example.generator;

public class Step {
    String action;
    String object;
    String field;

    public Step(String action, String object) {
        this.action = action;
        this.object = object;
    }

    public Step(String action, String object, String field) {
        this.action = action;
        this.object = object;
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
}
