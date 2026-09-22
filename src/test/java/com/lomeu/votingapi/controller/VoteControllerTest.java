package com.lomeu.votingapi.controller;

import com.lomeu.votingapi.service.VoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteController.class)
@Import(com.lomeu.votingapi.config.TimeConfig.class)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VoteService voteService;

    @Test
    void registersVote() throws Exception {
        mockMvc.perform(post("/api/v1/topics/1/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\":\"member-123\",\"vote\":\"YES\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Vote registered successfully"));
    }
}
