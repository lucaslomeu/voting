package com.lomeu.votingapi.service;

import com.lomeu.votingapi.dto.request.OpenVotingSessionRequest;
import com.lomeu.votingapi.entity.Topic;
import com.lomeu.votingapi.entity.VotingSession;
import com.lomeu.votingapi.exception.BusinessException;
import com.lomeu.votingapi.exception.ErrorCode;
import com.lomeu.votingapi.repository.TopicRepository;
import com.lomeu.votingapi.repository.VotingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VotingSessionServiceTest {

    private final TopicRepository topicRepository = mock(TopicRepository.class);
    private final VotingSessionRepository sessionRepository = mock(VotingSessionRepository.class);
    private final Instant currentTime = Instant.parse("2026-09-20T15:00:00Z");
    private final Topic topic = new Topic("Budget", currentTime);
    private final VotingSessionService service = new VotingSessionService(
            topicRepository,
            sessionRepository,
            Clock.fixed(currentTime, ZoneOffset.UTC)
    );

    @BeforeEach
    void configureRepositories() {
        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(sessionRepository.findByTopicId(1L)).thenReturn(Optional.empty());
        when(sessionRepository.saveAndFlush(any(VotingSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void usesSixtySecondsByDefault() {
        VotingSession session = service.openSession(1L, new OpenVotingSessionRequest(null));

        assertThat(session.getOpenedAt()).isEqualTo(currentTime);
        assertThat(session.getExpiresAt()).isEqualTo(currentTime.plusSeconds(60));
    }

    @Test
    void rejectsInvalidDuration() {
        assertThatThrownBy(() -> service.openSession(1L, new OpenVotingSessionRequest(0L)))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).errorCode())
                .isEqualTo(ErrorCode.INVALID_DURATION);
    }

    @Test
    void rejectsSecondSession() {
        when(sessionRepository.findByTopicId(1L)).thenReturn(Optional.of(mock(VotingSession.class)));

        assertThatThrownBy(() -> service.openSession(1L, new OpenVotingSessionRequest(null)))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).errorCode())
                .isEqualTo(ErrorCode.VOTING_SESSION_ALREADY_EXISTS);
    }
}
