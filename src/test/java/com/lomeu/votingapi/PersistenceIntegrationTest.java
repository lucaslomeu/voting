package com.lomeu.votingapi;

import com.lomeu.votingapi.entity.Topic;
import com.lomeu.votingapi.entity.Vote;
import com.lomeu.votingapi.entity.VoteChoice;
import com.lomeu.votingapi.entity.VotingSession;
import com.lomeu.votingapi.dto.response.VotingResultResponse;
import com.lomeu.votingapi.repository.TopicRepository;
import com.lomeu.votingapi.repository.VoteRepository;
import com.lomeu.votingapi.repository.VotingSessionRepository;
import com.lomeu.votingapi.service.VotingResultService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class PersistenceIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("voting")
            .withUsername("voting")
            .withPassword("voting");

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private VotingSessionRepository votingSessionRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VotingResultService votingResultService;

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    void persistsEntitiesAndAppliesDatabaseConstraints() {
        Topic topic = topicRepository.saveAndFlush(new Topic(
                "Approval of the budget",
                Instant.parse("2026-09-20T15:00:00Z")
        ));
        VotingSession session = votingSessionRepository.saveAndFlush(new VotingSession(
                topic,
                Instant.parse("2026-09-20T15:00:00Z"),
                Instant.parse("2026-09-20T15:01:00Z")
        ));

        voteRepository.saveAndFlush(new Vote(
                topic,
                "member-123",
                VoteChoice.YES,
                Instant.parse("2026-09-20T15:00:01Z")
        ));

        assertThat(topicRepository.findById(topic.getId())).isPresent();
        assertThat(votingSessionRepository.findById(session.getId())).isPresent();
        assertThat(voteRepository.count()).isEqualTo(1);
        VotingResultResponse result = votingResultService.getResult(topic.getId());
        assertThat(result.totalVotes()).isEqualTo(1);
        assertThat(result.yesVotes()).isEqualTo(1);
        assertThat(result.noVotes()).isZero();
        assertThatThrownBy(() -> voteRepository.saveAndFlush(new Vote(
                topic,
                "member-123",
                VoteChoice.NO,
                Instant.parse("2026-09-20T15:00:02Z")
        ))).isInstanceOf(DataIntegrityViolationException.class);
    }
}
