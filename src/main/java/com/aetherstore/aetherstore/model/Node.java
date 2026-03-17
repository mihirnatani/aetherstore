package com.aetherstore.aetherstore.model;

public class Node {

    private String id;
    private String url;

    public Node(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}