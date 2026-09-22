package com.lomeu.votingapi.dto.request;

import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to open a voting session")
public record OpenVotingSessionRequest(
        @Schema(description = "Session duration in seconds. Defaults to 60 when omitted.", example = "60")
        @Positive(message = "Duration must be greater than zero")
        Long durationInSeconds
) {
}
