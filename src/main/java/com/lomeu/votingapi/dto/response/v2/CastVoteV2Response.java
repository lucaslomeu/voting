package com.lomeu.votingapi.dto.response.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "CPF-verified vote response")
public record CastVoteV2Response(
        @Schema(example = "ABLE_TO_VOTE")
        String status,
        @Schema(example = "Vote registered successfully")
        String message
) {
}
