package com.lomeu.votingapi.service;

import com.lomeu.votingapi.dto.request.CastVoteRequest;
import com.lomeu.votingapi.entity.Topic;
import com.lomeu.votingapi.entity.Vote;
import com.lomeu.votingapi.entity.VoteChoice;
import com.lomeu.votingapi.entity.VotingSession;
import com.lomeu.votingapi.exception.BusinessException;
import com.lomeu.votingapi.exception.ConstraintViolationDetector;
import com.lomeu.votingapi.exception.ErrorCode;
import com.lomeu.votingapi.repository.TopicRepository;
import com.lomeu.votingapi.repository.VoteRepository;
import com.lomeu.votingapi.repository.VotingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class VoteService {

    private static final Logger log = LoggerFactory.getLogger(VoteService.class);

    private final TopicRepository topicRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;
    private final Clock clock;

    public VoteService(
            TopicRepository topicRepository,
            VotingSessionRepository votingSessionRepository,
            VoteRepository voteRepository,
            Clock clock
    ) {
        this.topicRepository = topicRepository;
        this.votingSessionRepository = votingSessionRepository;
        this.voteRepository = voteRepository;
        this.clock = clock;
    }

    @Transactional
    public void castVote(Long topicId, CastVoteRequest request) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        VotingSession session = votingSessionRepository.findByTopicId(topicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VOTING_SESSION_NOT_FOUND));

        Instant now = clock.instant();
        if (!now.isBefore(session.getExpiresAt())) {
            throw new BusinessException(ErrorCode.VOTING_SESSION_CLOSED);
        }

        VoteChoice voteChoice = parseVote(request.vote());

        if (voteRepository.existsByTopicIdAndMemberId(topicId, request.memberId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_VOTE);
        }

        try {
            voteRepository.saveAndFlush(new Vote(
                    topic,
                    request.memberId(),
                    voteChoice,
                    now
            ));
            log.info("Vote registered for topic {}", topicId);
        } catch (DataIntegrityViolationException exception) {
            if (ConstraintViolationDetector.contains(exception, "uk_votes_topic_member")) {
                throw new BusinessException(ErrorCode.DUPLICATE_VOTE);
            }
            throw exception;
        }
    }

    private VoteChoice parseVote(String value) {
        try {
            return VoteChoice.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.INVALID_VOTE);
        }
    }
}
