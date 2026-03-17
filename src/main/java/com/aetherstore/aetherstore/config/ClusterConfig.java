package com.aetherstore.aetherstore.config;

import com.aetherstore.aetherstore.cluster.NodeRegistry;
import com.aetherstore.aetherstore.cluster.ConsistentHashRing;
import com.aetherstore.aetherstore.model.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class ClusterConfig {

    @Value("${cluster.nodes}")
    private String nodes;

    private final NodeRegistry registry;
    private final ConsistentHashRing hashRing;

    public ClusterConfig(NodeRegistry registry, ConsistentHashRing hashRing) {
        this.registry = registry;
        this.hashRing = hashRing;
    }

    @PostConstruct
    public void init() {

        String[] nodeList = nodes.split(",");

        int id = 1;

        for (String url : nodeList) {
            Node node = new Node("node" + id, url);
            registry.register(node);
            System.out.println("Registered node: " + node.getId() + " -> " + node.getUrl());
            id++;
        }
        hashRing.initialize();
    }
}