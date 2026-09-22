package com.lomeu.votingapi.service;

import com.lomeu.votingapi.exception.BusinessException;
import com.lomeu.votingapi.exception.ErrorCode;
import com.lomeu.votingapi.entity.VoteChoice;
import com.lomeu.votingapi.repository.TopicRepository;
import com.lomeu.votingapi.repository.VoteCountProjection;
import com.lomeu.votingapi.repository.VoteRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VotingResultServiceTest {

    private final TopicRepository topicRepository = mock(TopicRepository.class);
    private final VoteRepository voteRepository = mock(VoteRepository.class);
    private final VotingResultService service = new VotingResultService(topicRepository, voteRepository);

    @Test
    void returnsAggregatedCounts() {
        when(topicRepository.existsById(1L)).thenReturn(true);
        VoteCountProjection projection = projection(10L, 7L, 3L);
        when(voteRepository.aggregateByTopicId(1L, VoteChoice.YES, VoteChoice.NO)).thenReturn(projection);

        var result = service.getResult(1L);
        assertThat(result.totalVotes()).isEqualTo(10);
        assertThat(result.yesVotes()).isEqualTo(7);
        assertThat(result.noVotes()).isEqualTo(3);
    }

    @Test
    void returnsZeroCountsWhenThereAreNoVotes() {
        when(topicRepository.existsById(1L)).thenReturn(true);
        VoteCountProjection projection = projection(0L, 0L, 0L);
        when(voteRepository.aggregateByTopicId(1L, VoteChoice.YES, VoteChoice.NO)).thenReturn(projection);

        assertThat(service.getResult(1L).totalVotes()).isZero();
    }

    @Test
    void rejectsUnknownTopic() {
        when(topicRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.getResult(99L))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).errorCode())
                .isEqualTo(ErrorCode.TOPIC_NOT_FOUND);
    }

    private VoteCountProjection projection(Long total, Long yes, Long no) {
        VoteCountProjection projection = mock(VoteCountProjection.class);
        when(projection.getTotalVotes()).thenReturn(total);
        when(projection.getYesVotes()).thenReturn(yes);
        when(projection.getNoVotes()).thenReturn(no);
        return projection;
    }
}
