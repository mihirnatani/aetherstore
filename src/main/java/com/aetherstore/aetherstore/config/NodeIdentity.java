package com.aetherstore.aetherstore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NodeIdentity {

    @Value("${node.id}")
    private String nodeId;

    public String getNodeId() {
        return nodeId;
    }
}