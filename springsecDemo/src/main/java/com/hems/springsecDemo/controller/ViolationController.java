package com.hems.springsecDemo.controller;

import com.hems.springsecDemo.model.Violation;
import com.hems.springsecDemo.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("violations")
public class ViolationController {

    @Autowired
    private ViolationService service;

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Violation v) {
        return ResponseEntity.ok(service.save(v));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}