package com.lomeu.votingapi;

import com.lomeu.votingapi.repository.TopicRepository;
import com.lomeu.votingapi.repository.VoteRepository;
import com.lomeu.votingapi.repository.VotingSessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
class VotingApiApplicationTests {

    @MockitoBean
    private TopicRepository topicRepository;

    @MockitoBean
    private VotingSessionRepository votingSessionRepository;

    @MockitoBean
    private VoteRepository voteRepository;

    @Test
    void contextLoads() {
    }
}
