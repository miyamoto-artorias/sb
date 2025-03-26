package com.demo.sb.controllers;


import com.demo.sb.entity.Connection;
import com.demo.sb.service.ConnectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {
    @Autowired
    private ConnectionService connectionService;

    @PostMapping
    public ResponseEntity<Connection> createConnection(@Valid @RequestBody Connection connection) {
        Connection savedConnection = connectionService.createConnection(connection); // Assume this method exists
        return ResponseEntity.ok(savedConnection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Connection> getConnectionById(@PathVariable int id) {
        Optional<Connection> connection = connectionService.findById(id); // Assume this method exists
        return connection.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Connection>> getConnectionsByUser(@PathVariable int userId) {
        List<Connection> connections = connectionService.getConnectionsByUser(userId); // Assume this method exists
        return ResponseEntity.ok(connections);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptConnection(@PathVariable int id) {
        connectionService.acceptConnection(id); // Assume this method exists
        return ResponseEntity.ok().build();
    }
}