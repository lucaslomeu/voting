package com.lomeu.votingapi.controller;

import com.lomeu.votingapi.dto.request.OpenVotingSessionRequest;
import com.lomeu.votingapi.dto.response.VotingSessionResponse;
import com.lomeu.votingapi.service.VotingSessionService;
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
@RequestMapping("/api/v1/topics/{topicId}/sessions")
@Tag(name = "Voting sessions", description = "Voting session management")
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    public VotingSessionController(VotingSessionService votingSessionService) {
        this.votingSessionService = votingSessionService;
    }

    @PostMapping
    @Operation(
            summary = "Open a voting session",
            description = "Opens one voting session for a topic. The default duration is 60 seconds."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Voting session opened",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VotingSessionResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "topicId": 1,
                                      "openedAt": "2026-09-20T15:00:00Z",
                                      "expiresAt": "2026-09-20T15:01:00Z"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid duration or request",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Topic not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Voting session already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<VotingSessionResponse> openSession(
            @Parameter(description = "Topic identifier", example = "1")
            @PathVariable Long topicId,
            @Valid @RequestBody OpenVotingSessionRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(VotingSessionResponse.from(votingSessionService.openSession(topicId, request)));
    }
}
