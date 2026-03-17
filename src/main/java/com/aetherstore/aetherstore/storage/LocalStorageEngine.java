package com.aetherstore.aetherstore.storage;

import com.aetherstore.aetherstore.model.ValueObject;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocalStorageEngine {

    private final Map<String, ValueObject> store = new ConcurrentHashMap<>();

    public void put(String key, ValueObject value) {

        store.put(key, value);

    }

    public ValueObject get(String key) {

        return store.get(key);

    }

    public boolean containsKey(String key) {

        return store.containsKey(key);

    }

}