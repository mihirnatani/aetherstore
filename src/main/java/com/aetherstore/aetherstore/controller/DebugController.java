package com.aetherstore.aetherstore.controller;

import com.aetherstore.aetherstore.cluster.ConsistentHashRing;
import com.aetherstore.aetherstore.model.Node;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final ConsistentHashRing hashRing;

    public DebugController(ConsistentHashRing hashRing) {
        this.hashRing = hashRing;
    }

    @GetMapping("/key/{key}")
    public Map<String, Object> debugKey(@PathVariable String key) {

        Map<String, Object> result = new HashMap<>();

        Node primary = hashRing.getNodeForKey(key);

        List<Node> replicas = hashRing.getReplicaNodes(key, 3);

        result.put("key", key);
        result.put("hash", Math.abs(key.hashCode()));
        result.put("primaryNode", primary.getId());

        List<String> replicaIds = new ArrayList<>();

        for (Node node : replicas) {
            replicaIds.add(node.getId());
        }

        result.put("replicas", replicaIds);

        return result;
    }
}