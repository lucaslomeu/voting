package com.lomeu.votingapi.controller;

import com.lomeu.votingapi.dto.response.VotingResultResponse;
import com.lomeu.votingapi.service.VotingResultService;
import com.lomeu.votingapi.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/topics/{topicId}/result")
@Tag(name = "Results", description = "Voting result queries")
public class VotingResultController {

    private final VotingResultService votingResultService;

    public VotingResultController(VotingResultService votingResultService) {
        this.votingResultService = votingResultService;
    }

    @GetMapping
    @Operation(
            summary = "Get voting result",
            description = "Returns the aggregated result while the session is open or after it is closed."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Voting result returned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VotingResultResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "topicId": 1,
                                      "totalVotes": 10,
                                      "yesVotes": 7,
                                      "noVotes": 3
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Topic not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<VotingResultResponse> getResult(
            @Parameter(description = "Topic identifier", example = "1")
            @PathVariable Long topicId
    ) {
        return ResponseEntity.ok(votingResultService.getResult(topicId));
    }
}
