package com.lomeu.votingapi.repository;

import com.lomeu.votingapi.entity.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    Optional<VotingSession> findByTopicId(Long topicId);
}
