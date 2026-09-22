package com.lomeu.votingapi.service;

import com.lomeu.votingapi.dto.request.OpenVotingSessionRequest;
import com.lomeu.votingapi.entity.Topic;
import com.lomeu.votingapi.entity.VotingSession;
import com.lomeu.votingapi.exception.BusinessException;
import com.lomeu.votingapi.exception.ConstraintViolationDetector;
import com.lomeu.votingapi.exception.ErrorCode;
import com.lomeu.votingapi.repository.TopicRepository;
import com.lomeu.votingapi.repository.VotingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class VotingSessionService {

    private static final long DEFAULT_DURATION_IN_SECONDS = 60L;
    private static final Logger log = LoggerFactory.getLogger(VotingSessionService.class);

    private final TopicRepository topicRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final Clock clock;

    public VotingSessionService(
            TopicRepository topicRepository,
            VotingSessionRepository votingSessionRepository,
            Clock clock
    ) {
        this.topicRepository = topicRepository;
        this.votingSessionRepository = votingSessionRepository;
        this.clock = clock;
    }

    @Transactional
    public VotingSession openSession(Long topicId, OpenVotingSessionRequest request) {
        long durationInSeconds = resolveDuration(request);
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));

        if (votingSessionRepository.findByTopicId(topicId).isPresent()) {
            throw new BusinessException(ErrorCode.VOTING_SESSION_ALREADY_EXISTS);
        }

        Instant openedAt = clock.instant();
        VotingSession session = new VotingSession(
                topic,
                openedAt,
                openedAt.plusSeconds(durationInSeconds)
        );

        try {
            VotingSession savedSession = votingSessionRepository.saveAndFlush(session);
            log.info("Voting session opened for topic {}", topicId);
            return savedSession;
        } catch (DataIntegrityViolationException exception) {
            if (ConstraintViolationDetector.contains(exception, "uk_voting_sessions_topic")) {
                throw new BusinessException(ErrorCode.VOTING_SESSION_ALREADY_EXISTS);
            }
            throw exception;
        }
    }

    private long resolveDuration(OpenVotingSessionRequest request) {
        if (request == null || request.durationInSeconds() == null) {
            return DEFAULT_DURATION_IN_SECONDS;
        }
        if (request.durationInSeconds() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DURATION);
        }
        return request.durationInSeconds();
    }
}
