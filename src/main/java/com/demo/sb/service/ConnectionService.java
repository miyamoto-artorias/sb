package com.demo.sb.service;


import com.demo.sb.entity.Category;
import com.demo.sb.entity.Connection;
import com.demo.sb.entity.User;
import com.demo.sb.repository.ConnectionRepository;
import com.demo.sb.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConnectionService {
    @Autowired private ConnectionRepository connectionRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Connection createConnection(int user1Id, int user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new EntityNotFoundException("User1 not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new EntityNotFoundException("User2 not found"));
        Connection connection = new Connection();
        connection.setUser1(user1);
        connection.setUser2(user2);
        connection.setStatus("pending");
        connection.setRequested(LocalDateTime.now());
        return connectionRepository.save(connection);
    }
    public Connection findById(int connectionId) {
        return connectionRepository.findById(connectionId)
                .orElseThrow(() -> new EntityNotFoundException("Connection with ID " + connectionId + " not found"));
    }

    // Accept a connection
    @Transactional
    public Connection acceptConnection(int connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new EntityNotFoundException("Connection with ID " + connectionId + " not found"));
        if (!"pending".equals(connection.getStatus())) {
            throw new IllegalStateException("Connection is not in a pending state");
        }
        connection.setStatus("accepted");
        connection.setConfirmed(LocalDateTime.now());
        return connectionRepository.save(connection);
    }

    public List<Connection> getConnectionsByUser(int userId) {
        return connectionRepository.findByUser1IdOrUser2Id(userId, userId);
    }


}