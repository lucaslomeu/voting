package com.lomeu.votingapi;

import com.lomeu.votingapi.dto.request.CastVoteRequest;
import com.lomeu.votingapi.entity.Topic;
import com.lomeu.votingapi.entity.VotingSession;
import com.lomeu.votingapi.exception.BusinessException;
import com.lomeu.votingapi.exception.ErrorCode;
import com.lomeu.votingapi.repository.TopicRepository;
import com.lomeu.votingapi.repository.VoteRepository;
import com.lomeu.votingapi.repository.VotingSessionRepository;
import com.lomeu.votingapi.service.VoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class VoteConcurrencyIntegrationTest {

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
    private VoteService voteService;

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    void onlyOneConcurrentVoteForSameMemberIsPersisted() throws Exception {
        Topic topic = topicRepository.saveAndFlush(new Topic(
                "Concurrent vote",
                Instant.now()
        ));
        votingSessionRepository.saveAndFlush(new VotingSession(
                topic,
                Instant.now(),
                Instant.now().plusSeconds(60)
        ));

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<Boolean> first = executor.submit(() -> castVote(ready, start, topic.getId()));
            Future<Boolean> second = executor.submit(() -> castVote(ready, start, topic.getId()));

            ready.await();
            start.countDown();

            assertThat(List.of(first.get(), second.get()))
                    .containsExactlyInAnyOrder(true, false);
            assertThat(voteRepository.count()).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }
    }

    private boolean castVote(CountDownLatch ready, CountDownLatch start, Long topicId)
            throws InterruptedException {
        ready.countDown();
        start.await();
        try {
            voteService.castVote(topicId, new CastVoteRequest("member-123", "YES"));
            return true;
        } catch (BusinessException exception) {
            if (exception.errorCode() == ErrorCode.DUPLICATE_VOTE) {
                return false;
            }
            throw exception;
        }
    }
}
