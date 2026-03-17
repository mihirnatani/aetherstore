package com.aetherstore.aetherstore.service;

import com.aetherstore.aetherstore.model.Node;
import com.aetherstore.aetherstore.model.ValueObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReplicationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void replicate(Node node, String key, ValueObject value) {

        String url = node.getUrl() + "/internal/replicate?key=" + key;

        restTemplate.postForObject(url, value, Void.class);
    }

    public ValueObject read(Node node, String key) {

        String url = node.getUrl() + "/internal/read/" + key;

        return restTemplate.getForObject(url, ValueObject.class);
    }
}