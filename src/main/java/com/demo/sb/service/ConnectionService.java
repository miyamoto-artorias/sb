package com.demo.sb.service;


import com.demo.sb.entity.Connection;
import com.demo.sb.repository.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConnectionService {
    @Autowired
    private ConnectionRepository connectionRepository;

    @Transactional
    public Connection createConnection(Connection connection) {
        connection.setRequested(LocalDateTime.now());
        connection.setStatus("pending");
        return connectionRepository.save(connection);
    }

    public Optional<Connection> findById(int id) {
        return connectionRepository.findById(id);
    }

    public List<Connection> getConnectionsByUser(int userId) {
        return connectionRepository.findByUser1IdOrUser2Id(userId, userId);
    }

    @Transactional
    public void acceptConnection(int id) {
        Optional<Connection> connection = connectionRepository.findById(id);
        if (connection.isPresent()) {
            connection.get().setStatus("accepted");
            connection.get().setConfirmed(LocalDateTime.now());
            connectionRepository.save(connection.get());
        } else {
            throw new RuntimeException("Connection not found");
        }
    }
}