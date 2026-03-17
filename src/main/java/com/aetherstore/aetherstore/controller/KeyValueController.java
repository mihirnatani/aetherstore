package com.aetherstore.aetherstore.controller;

import com.aetherstore.aetherstore.model.ValueObject;
import com.aetherstore.aetherstore.service.CoordinatorService;
import com.aetherstore.aetherstore.storage.LocalStorageEngine;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
public class KeyValueController {

    private final CoordinatorService coordinator;

    public KeyValueController(CoordinatorService coordinator) {
        this.coordinator = coordinator;
    }
    @GetMapping("/{key}")
    public ValueObject get(@PathVariable String key) {

        return coordinator.get(key);

    }
    @PutMapping("/{key}")
    public String put(@PathVariable String key,
                      @RequestBody String value) {

        coordinator.put(key, value);

        return "Write replicated successfully";
    }
}