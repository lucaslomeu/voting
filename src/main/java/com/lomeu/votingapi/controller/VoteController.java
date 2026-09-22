package com.lomeu.votingapi.controller;

import com.lomeu.votingapi.dto.request.CastVoteRequest;
import com.lomeu.votingapi.dto.response.VoteResponse;
import com.lomeu.votingapi.service.VoteService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.lomeu.votingapi.exception.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/topics/{topicId}/votes")
@Tag(name = "Votes", description = "Vote registration")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    @Operation(
            summary = "Register a vote",
            description = "Registers one YES or NO vote for a member while the topic session is open."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Vote registered",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VoteResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "Vote registered successfully"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid member or vote",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Topic or voting session not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Voting session closed or duplicate vote",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<VoteResponse> castVote(
            @Parameter(description = "Topic identifier", example = "1")
            @PathVariable Long topicId,
            @Valid @RequestBody CastVoteRequest request
    ) {
        voteService.castVote(topicId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new VoteResponse("Vote registered successfully"));
    }
}
