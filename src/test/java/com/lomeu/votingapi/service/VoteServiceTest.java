package com.lomeu.votingapi.service;

import com.lomeu.votingapi.dto.request.CastVoteRequest;
import com.lomeu.votingapi.entity.Topic;
import com.lomeu.votingapi.entity.Vote;
import com.lomeu.votingapi.entity.VotingSession;
import com.lomeu.votingapi.exception.BusinessException;
import com.lomeu.votingapi.exception.ErrorCode;
import com.lomeu.votingapi.repository.TopicRepository;
import com.lomeu.votingapi.repository.VoteRepository;
import com.lomeu.votingapi.repository.VotingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VoteServiceTest {

    private final TopicRepository topicRepository = mock(TopicRepository.class);
    private final VotingSessionRepository sessionRepository = mock(VotingSessionRepository.class);
    private final VoteRepository voteRepository = mock(VoteRepository.class);
    private final Instant currentTime = Instant.parse("2026-09-20T15:00:00Z");
    private final Topic topic = new Topic("Budget", currentTime);
    private final VoteService service = new VoteService(
            topicRepository,
            sessionRepository,
            voteRepository,
            Clock.fixed(currentTime, ZoneOffset.UTC)
    );

    @BeforeEach
    void configureRepositories() {
        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(sessionRepository.findByTopicId(1L)).thenReturn(Optional.of(
                new VotingSession(topic, currentTime.minusSeconds(1), currentTime.plusSeconds(60))
        ));
        when(voteRepository.existsByTopicIdAndMemberId(1L, "member-123")).thenReturn(false);
        when(voteRepository.saveAndFlush(any(Vote.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void registersVote() {
        service.castVote(1L, new CastVoteRequest("member-123", "YES"));
    }

    @Test
    void rejectsInvalidVote() {
        assertThatThrownBy(() -> service.castVote(1L, new CastVoteRequest("member-123", "MAYBE")))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).errorCode())
                .isEqualTo(ErrorCode.INVALID_VOTE);
    }

    @Test
    void rejectsClosedSession() {
        when(sessionRepository.findByTopicId(1L)).thenReturn(Optional.of(
                new VotingSession(topic, currentTime.minusSeconds(60), currentTime)
        ));

        assertThatThrownBy(() -> service.castVote(1L, new CastVoteRequest("member-123", "YES")))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).errorCode())
                .isEqualTo(ErrorCode.VOTING_SESSION_CLOSED);
    }

    @Test
    void translatesUniqueConstraintViolationToDuplicateVote() {
        when(voteRepository.saveAndFlush(any(Vote.class)))
                .thenThrow(new DataIntegrityViolationException("uk_votes_topic_member duplicate key"));

        assertThatThrownBy(() -> service.castVote(1L, new CastVoteRequest("member-123", "YES")))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).errorCode())
                .isEqualTo(ErrorCode.DUPLICATE_VOTE);
    }
}
