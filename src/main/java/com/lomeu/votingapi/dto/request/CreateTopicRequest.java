package com.lomeu.votingapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to create a topic")
public record CreateTopicRequest(
        @Schema(description = "Topic title", example = "Approval of the 2026 budget", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title
) {
}
