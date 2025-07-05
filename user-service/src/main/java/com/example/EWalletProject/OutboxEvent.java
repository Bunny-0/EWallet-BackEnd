package com.example.EWalletProject;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "outbox_events", indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_topic", columnList = "eventType"),
        @Index(name = "idx_created_at", columnList = "createdAt")
})
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String status;

    private LocalDateTime createdAt = LocalDateTime.now();
}

