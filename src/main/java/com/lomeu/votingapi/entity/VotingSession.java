package com.lomeu.votingapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "voting_sessions")
public class VotingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false, unique = true)
    private Topic topic;

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    protected VotingSession() {
    }

    public VotingSession(Topic topic, Instant openedAt, Instant expiresAt) {
        this.topic = topic;
        this.openedAt = openedAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
