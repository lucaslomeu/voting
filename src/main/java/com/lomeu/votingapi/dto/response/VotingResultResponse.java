package com.lomeu.votingapi.dto.response;

import com.lomeu.votingapi.repository.VoteCountProjection;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aggregated voting result")
public record VotingResultResponse(
        @Schema(example = "1")
        Long topicId,
        @Schema(example = "10")
        Long totalVotes,
        @Schema(example = "7")
        Long yesVotes,
        @Schema(example = "3")
        Long noVotes
) {

    public static VotingResultResponse from(Long topicId, VoteCountProjection projection) {
        return new VotingResultResponse(
                topicId,
                projection.getTotalVotes(),
                projection.getYesVotes(),
                projection.getNoVotes()
        );
    }
}
