package com.aetherstore.aetherstore.service;
import java.util.Map;
import java.util.HashMap;

import com.aetherstore.aetherstore.cluster.ConsistentHashRing;
import com.aetherstore.aetherstore.model.Node;
import com.aetherstore.aetherstore.config.NodeIdentity;
import com.aetherstore.aetherstore.model.ValueObject;
import com.aetherstore.aetherstore.storage.LocalStorageEngine;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoordinatorService {

    private static final int REPLICATION_FACTOR = 3;

    private final ConsistentHashRing hashRing;
    private final ReplicationService replicationService;
    private final LocalStorageEngine storage;
    private final NodeIdentity nodeIdentity;
    private static final int WRITE_QUORUM = 2;
    private static final int READ_QUORUM = 2;

    public CoordinatorService(ConsistentHashRing hashRing,
                              ReplicationService replicationService,
                              LocalStorageEngine storage,
                              NodeIdentity nodeIdentity) {

        this.hashRing = hashRing;
        this.replicationService = replicationService;
        this.storage = storage;
        this.nodeIdentity = nodeIdentity;
    }

    public void put(String key, String value) {
        System.out.println("Writing key: " + key);
        ValueObject vo = new ValueObject(value, System.currentTimeMillis());
        List<Node> replicas = hashRing.getReplicaNodes(key, REPLICATION_FACTOR);

        System.out.println("Replicas selected for key: " + key);

        for (Node n : replicas) {
            System.out.println(n.getId() + " -> " + n.getUrl());
        }

        int successCount = 0;
        for (Node node : replicas) {
            try {
                if (node.getId().equals(nodeIdentity.getNodeId())) {
                    storage.put(key, vo);
                    successCount++;
                } else {
                    replicationService.replicate(node, key, vo);
                    successCount++;
                }
            } catch (Exception e) {
                System.out.println("Replication failed for node " + node.getId());
            }
        }
        if (successCount < WRITE_QUORUM) {
            throw new RuntimeException("Write quorum not satisfied");
        }
    }
    private boolean isLocalNode(Node node) {
        return node.getUrl().contains("8081");
    }

    private ValueObject findLatestVersion(List<ValueObject> values) {
        ValueObject latest = null;
        for (ValueObject value : values) {
            if (latest == null || value.getVersion() > latest.getVersion()) {
                latest = value;
            }
        }
        return latest;
    }

    private void performReadRepair(String key,
                                   ValueObject latest,
                                   Map<Node, ValueObject> responses) {

        for (Map.Entry<Node, ValueObject> entry : responses.entrySet()) {

            Node node = entry.getKey();
            ValueObject value = entry.getValue();

            if (value.getVersion() < latest.getVersion()) {

                try {

                    if (node.getId().equals(nodeIdentity.getNodeId())) {

                        storage.put(key, latest);

                    } else {

                        replicationService.replicate(node, key, latest);

                    }

                    System.out.println("Read repair executed for node " + node.getId());

                } catch (Exception e) {

                    System.out.println("Read repair failed for node " + node.getId());

                }

            }
        }
    }

    public ValueObject get(String key) {
        List<Node> replicas = hashRing.getReplicaNodes(key, REPLICATION_FACTOR);
        Map<Node, ValueObject> responses = new HashMap<>();
        int responsesReceived = 0;
        for (Node node : replicas) {
            try {
                ValueObject value;
                if (node.getId().equals(nodeIdentity.getNodeId())) {
                    value = storage.get(key);
                } else {
                    value = replicationService.read(node, key);
                }
                if (value != null) {
                    responses.put(node, value);
                }
                responsesReceived++;
                if (responsesReceived >= READ_QUORUM) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Read failed for node " + node.getId());
            }
        }
        ValueObject latest = findLatestVersion(new ArrayList<>(responses.values()));
        performReadRepair(key, latest, responses);
        return latest;
    }
}