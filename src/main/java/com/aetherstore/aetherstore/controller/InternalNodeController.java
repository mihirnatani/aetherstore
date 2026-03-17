package com.aetherstore.aetherstore.controller;

import com.aetherstore.aetherstore.model.ValueObject;
import com.aetherstore.aetherstore.storage.LocalStorageEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class InternalNodeController {

    private final LocalStorageEngine storage;

    public InternalNodeController(LocalStorageEngine storage) {
        this.storage = storage;
    }

    @PostMapping("/replicate")
    public void replicate(@RequestParam String key,
                          @RequestBody ValueObject value) {

        storage.put(key, value);
    }

    @GetMapping("/read/{key}")
    public ResponseEntity<ValueObject> read(@PathVariable String key) {
        ValueObject value = storage.get(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }
}