package org.example.testscript;

public class Property {
    private String key;
    private String value;

    public Property(String key) {
        this.key = key;
        this.value = "";
    }
    public Property(String key, String value) {
        this.value = value;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}