package com.lomeu.votingapi.dto.request.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to register a vote using CPF eligibility verification")
public record CastVoteV2Request(
        @Schema(description = "CPF used for eligibility verification", example = "12345678901", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "CPF must not be blank")
        String cpf,
        @Schema(description = "Vote choice", allowableValues = {"YES", "NO"}, example = "YES", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Vote must not be blank")
        String vote
) {
}
