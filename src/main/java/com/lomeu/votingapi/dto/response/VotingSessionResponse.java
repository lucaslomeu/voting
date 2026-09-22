package com.lomeu.votingapi.dto.response;

import com.lomeu.votingapi.entity.VotingSession;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Opened voting session")
public record VotingSessionResponse(
        @Schema(example = "1")
        Long id,
        @Schema(example = "1")
        Long topicId,
        @Schema(example = "2026-09-20T15:00:00Z")
        Instant openedAt,
        @Schema(example = "2026-09-20T15:01:00Z")
        Instant expiresAt
) {

    public static VotingSessionResponse from(VotingSession session) {
        return new VotingSessionResponse(
                session.getId(),
                session.getTopic().getId(),
                session.getOpenedAt(),
                session.getExpiresAt()
        );
    }
}
