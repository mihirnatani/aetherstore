package com.aetherstore.aetherstore.cluster;

import com.aetherstore.aetherstore.model.Node;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NodeRegistry {

    private final List<Node> nodes = new ArrayList<>();

    public void register(Node node) {
        nodes.add(node);
    }

    public List<Node> getAllNodes() {
        return nodes;
    }
}