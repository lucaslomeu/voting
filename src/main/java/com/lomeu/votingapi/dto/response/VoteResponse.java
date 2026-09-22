package com.lomeu.votingapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Vote registration response")
public record VoteResponse(String message) {
}
