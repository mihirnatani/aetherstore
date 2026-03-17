package com.aetherstore.aetherstore.model;

public class ValueObject {

    private String value;
    private long version;

    public ValueObject(String value, long version) {
        this.value = value;
        this.version = version;
    }

    public String getValue() {
        return value;
    }

    public long getVersion() {
        return version;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}