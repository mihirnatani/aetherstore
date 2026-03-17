# AetherStore

AetherStore is a **Dynamo-inspired distributed key-value store** built using **Spring Boot**.

The system demonstrates core concepts from the Amazon Dynamo paper including:

* Consistent Hashing
* Virtual Nodes
* Replication
* Quorum Reads/Writes
* Read Repair

---

# Architecture

AetherStore uses a **distributed cluster of nodes** where each node runs an independent Spring Boot instance.

Keys are distributed across nodes using a **consistent hash ring**.

Each key is replicated across multiple nodes to ensure **fault tolerance and availability**.

---

# System Design

Client requests are sent to a **Coordinator Node** which:

1. Determines the responsible nodes using **consistent hashing**
2. Replicates the data across replicas
3. Ensures **write quorum**
4. Serves reads using **read quorum**
5. Performs **read repair** if inconsistencies are detected

---

# Features

1. Consistent Hashing
2.  Virtual Nodes
3.  Replication (N = 3)
4.  Write Quorum (W = 2)
5.  Read Quorum (R = 2)
6.  Read Repair

---

# Cluster Setup

Run three nodes:

Node 1

```
--server.port=8081 --node.id=node1
```

Node 2

```
--server.port=8082 --node.id=node2
```

Node 3

```
--server.port=8083 --node.id=node3
```

---

# Example API

Write

```
PUT /store/user123
Body: mihir
```

Read

```
GET /store/user123
```

---

# DynamoDB Paper Concepts Implemented

| Concept            | Implemented |
| ------------------ | ----------- |
| Consistent Hashing | ✓           |
| Virtual Nodes      | ✓           |
| Replication        | ✓           |
| Quorum             | ✓           |
| Read Repair        | ✓           |

---

# Planned Improvements (v2)

* Hinted Handoff
* Vector Clocks
* Merkle Trees
* Gossip Protocol
* Automatic Node Failure Detection

---

# Tech Stack

* Java 24
* Spring Boot
* REST APIs
* Distributed Systems Concepts

---

# Inspiration

This project is inspired by the **Amazon DynamoDB paper**.

It was built to understand **distributed system fundamentals** and **database internals**.
