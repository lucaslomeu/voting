package com.lomeu.votingapi.controller.v2;

import com.lomeu.votingapi.client.cpf.CpfEligibilityClient;
import com.lomeu.votingapi.client.cpf.CpfEligibilityStatus;
import com.lomeu.votingapi.dto.request.CastVoteRequest;
import com.lomeu.votingapi.dto.request.v2.CastVoteV2Request;
import com.lomeu.votingapi.dto.response.v2.CastVoteV2Response;
import com.lomeu.votingapi.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/topics/{topicId}/votes")
@Tag(name = "Votes v2", description = "CPF-verified vote registration")
public class VoteV2Controller {

    private final CpfEligibilityClient cpfEligibilityClient;
    private final VoteService voteService;

    public VoteV2Controller(CpfEligibilityClient cpfEligibilityClient, VoteService voteService) {
        this.cpfEligibilityClient = cpfEligibilityClient;
        this.voteService = voteService;
    }

    @PostMapping
    @Operation(
            summary = "Register a CPF-verified vote",
            description = "Checks CPF eligibility before registering the vote. The v1 contract remains unchanged."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "CPF is eligible and vote registered",
                    content = @Content(
                            schema = @Schema(implementation = CastVoteV2Response.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": "ABLE_TO_VOTE",
                                      "message": "Vote registered successfully"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "CPF is not eligible or vote conflicts with session rules",
                    content = @Content(schema = @Schema(implementation = CastVoteV2Response.class))
            ),
            @ApiResponse(responseCode = "404", description = "CPF, topic or session not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<CastVoteV2Response> castVote(
            @Parameter(description = "Topic identifier", example = "1")
            @PathVariable Long topicId,
            @Valid @RequestBody CastVoteV2Request request
    ) {
        CpfEligibilityStatus status = cpfEligibilityClient.check(request.cpf());
        if (status == CpfEligibilityStatus.UNABLE_TO_VOTE) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new CastVoteV2Response(status.name(), null));
        }

        voteService.castVote(topicId, new CastVoteRequest(request.cpf(), request.vote()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CastVoteV2Response(status.name(), "Vote registered successfully"));
    }
}
