package com.lomeu.votingapi.controller;

import com.lomeu.votingapi.entity.Topic;
import com.lomeu.votingapi.entity.VotingSession;
import com.lomeu.votingapi.service.VotingSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VotingSessionController.class)
@Import(com.lomeu.votingapi.config.TimeConfig.class)
class VotingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotingSessionService votingSessionService;

    @Test
    void opensVotingSession() throws Exception {
        Topic topic = mock(Topic.class);
        when(topic.getId()).thenReturn(1L);
        VotingSession session = mock(VotingSession.class);
        when(session.getId()).thenReturn(10L);
        when(session.getTopic()).thenReturn(topic);
        when(session.getOpenedAt()).thenReturn(Instant.parse("2026-09-20T15:00:00Z"));
        when(session.getExpiresAt()).thenReturn(Instant.parse("2026-09-20T15:02:00Z"));
        when(votingSessionService.openSession(any(), any())).thenReturn(session);

        mockMvc.perform(post("/api/v1/topics/1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationInSeconds\":120}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.topicId").value(1))
                .andExpect(jsonPath("$.expiresAt").value("2026-09-20T15:02:00Z"));
    }
}
