package com.example.EWalletProject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent,Long> {

    List<OutboxEvent> findByStatus(String status);
}
