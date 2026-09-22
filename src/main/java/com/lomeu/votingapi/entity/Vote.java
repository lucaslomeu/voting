package com.lomeu.votingapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "votes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_votes_topic_member",
                columnNames = {"topic_id", "member_id"}
        )
)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private VoteChoice vote;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Vote() {
    }

    public Vote(Topic topic, String memberId, VoteChoice vote, Instant createdAt) {
        this.topic = topic;
        this.memberId = memberId;
        this.vote = vote;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }

    public String getMemberId() {
        return memberId;
    }

    public VoteChoice getVote() {
        return vote;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
