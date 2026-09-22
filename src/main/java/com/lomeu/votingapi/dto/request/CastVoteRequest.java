package com.lomeu.votingapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to register a member vote")
public record CastVoteRequest(
        @Schema(description = "Member identifier", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Member ID must not be blank")
        @Size(max = 255, message = "Member ID must not exceed 255 characters")
        String memberId,
        @Schema(description = "Vote choice", allowableValues = {"YES", "NO"}, example = "YES", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Vote must not be blank")
        String vote
) {
}
