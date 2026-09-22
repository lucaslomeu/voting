package com.lomeu.votingapi.controller;

import com.lomeu.votingapi.dto.response.VotingResultResponse;
import com.lomeu.votingapi.service.VotingResultService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VotingResultController.class)
@Import(com.lomeu.votingapi.config.TimeConfig.class)
class VotingResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotingResultService votingResultService;

    @Test
    void returnsVotingResult() throws Exception {
        when(votingResultService.getResult(1L))
                .thenReturn(new VotingResultResponse(1L, 10L, 7L, 3L));

        mockMvc.perform(get("/api/v1/topics/1/result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVotes").value(10))
                .andExpect(jsonPath("$.yesVotes").value(7))
                .andExpect(jsonPath("$.noVotes").value(3));
    }
}
