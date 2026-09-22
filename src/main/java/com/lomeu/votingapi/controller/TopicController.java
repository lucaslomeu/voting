package com.lomeu.votingapi.controller;

import com.lomeu.votingapi.dto.request.CreateTopicRequest;
import com.lomeu.votingapi.dto.response.TopicResponse;
import com.lomeu.votingapi.service.TopicService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lomeu.votingapi.exception.ApiError;

@RestController
@RequestMapping("/api/v1/topics")
@Tag(name = "Topics", description = "Topic management")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    @Operation(
            summary = "Create a topic",
            description = "Creates a topic that can receive votes."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Topic created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TopicResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "title": "Approval of the 2026 budget",
                                      "createdAt": "2026-09-20T15:00:00Z"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<TopicResponse> createTopic(
            @Valid @RequestBody CreateTopicRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TopicResponse.from(topicService.createTopic(request)));
    }
}
