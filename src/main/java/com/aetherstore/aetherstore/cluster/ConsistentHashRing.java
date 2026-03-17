package com.aetherstore.aetherstore.cluster;

import com.aetherstore.aetherstore.model.Node;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Component
public class ConsistentHashRing {

    private final TreeMap<Long, Node> ring = new TreeMap<>();

    private final NodeRegistry registry;

    private static final int VIRTUAL_NODE_COUNT = 10;

    public ConsistentHashRing(NodeRegistry registry) {
        this.registry = registry;
    }

    /**
     * Initialize the consistent hash ring after Spring creates the bean.
     */
    // Inside ConsistentHashRing.java
    public void initialize() {
        // 1. CLEAR the ring first to ensure we aren't appending to an old state
        ring.clear();

        List<Node> allNodes = registry.getAllNodes();

        // Debugging: Verify registry isn't empty here
        System.out.println("Building ring with " + allNodes.size() + " nodes from registry.");

        for (Node node : allNodes) {
            for (int i = 0; i < VIRTUAL_NODE_COUNT; i++) {
                // Using a more distinct delimiter to avoid hash collisions
                String virtualNodeId = node.getId() + "-vnode-" + i;
                long hash = hash(virtualNodeId);
                ring.put(hash, node);
            }
        }

        System.out.println("Consistent Hash Ring successfully initialized with "
                + ring.size() + " virtual nodes.");
    }

    /**
     * Find primary node responsible for a key
     */
    public Node getNodeForKey(String key) {

        long hash = hash(key);

        SortedMap<Long, Node> tailMap = ring.tailMap(hash);

        if (tailMap.isEmpty()) {
            return ring.firstEntry().getValue();
        }

        return tailMap.get(tailMap.firstKey());
    }

    /**
     * Simple hash function
     */
    private long hash(String key) {
        return Math.abs(key.hashCode());
    }

    /**
     * Returns N replica nodes for a given key
     */
    public List<Node> getReplicaNodes(String key, int replicationFactor) {

        List<Node> replicas = new ArrayList<>();

        if (ring.isEmpty()) {
            return replicas;
        }

        long hash = hash(key);

        SortedMap<Long, Node> tailMap = ring.tailMap(hash);

        Iterator<Node> iterator;

        if (tailMap.isEmpty()) {
            iterator = ring.values().iterator();
        } else {
            iterator = tailMap.values().iterator();
        }

        while (replicas.size() < replicationFactor && iterator.hasNext()) {

            Node node = iterator.next();

            if (!replicas.contains(node)) {
                replicas.add(node);
            }
        }

        if (replicas.size() < replicationFactor) {

            for (Node node : ring.values()) {

                if (!replicas.contains(node)) {
                    replicas.add(node);
                }

                if (replicas.size() == replicationFactor) {
                    break;
                }
            }
        }

        return replicas;
    }
}