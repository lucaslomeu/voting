package com.lomeu.votingapi.controller.v2;

import com.lomeu.votingapi.client.cpf.CpfEligibilityClient;
import com.lomeu.votingapi.client.cpf.CpfEligibilityStatus;
import com.lomeu.votingapi.service.VoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteV2Controller.class)
@Import(com.lomeu.votingapi.config.TimeConfig.class)
class VoteV2ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CpfEligibilityClient cpfEligibilityClient;

    @MockitoBean
    private VoteService voteService;

    @Test
    void registersVoteWhenCpfIsEligible() throws Exception {
        when(cpfEligibilityClient.check("12345678901")).thenReturn(CpfEligibilityStatus.ABLE_TO_VOTE);

        mockMvc.perform(post("/api/v2/topics/1/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpf\":\"12345678901\",\"vote\":\"YES\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ABLE_TO_VOTE"))
                .andExpect(jsonPath("$.message").value("Vote registered successfully"));

        verify(voteService).castVote(anyLong(), any());
    }

    @Test
    void doesNotRegisterVoteWhenCpfIsUnableToVote() throws Exception {
        when(cpfEligibilityClient.check("12345678901")).thenReturn(CpfEligibilityStatus.UNABLE_TO_VOTE);

        mockMvc.perform(post("/api/v2/topics/1/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpf\":\"12345678901\",\"vote\":\"YES\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("UNABLE_TO_VOTE"));
    }
}
