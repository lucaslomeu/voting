package com.lomeu.votingapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        description = "Standard API error response",
        example = """
                {
                  "status": 409,
                  "code": "DUPLICATE_VOTE",
                  "message": "The member has already voted on this topic.",
                  "timestamp": "2026-09-20T15:00:00Z"
                }
                """
)
public record ApiError(
        @Schema(example = "409")
        int status,
        @Schema(example = "DUPLICATE_VOTE")
        String code,
        @Schema(example = "The member has already voted on this topic.")
        String message,
        @Schema(example = "2026-09-20T15:00:00Z")
        Instant timestamp,
        @Schema(description = "Field-specific validation errors", nullable = true)
        Map<String, String> errors
) {
}
