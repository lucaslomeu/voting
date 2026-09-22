package com.lomeu.votingapi.dto.response;

import com.lomeu.votingapi.entity.Topic;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Created topic")
public record TopicResponse(
        @Schema(example = "1")
        Long id,
        @Schema(example = "Approval of the 2026 budget")
        String title,
        @Schema(example = "2026-09-20T15:00:00Z")
        Instant createdAt
) {

    public static TopicResponse from(Topic topic) {
        return new TopicResponse(topic.getId(), topic.getTitle(), topic.getCreatedAt());
    }
}
