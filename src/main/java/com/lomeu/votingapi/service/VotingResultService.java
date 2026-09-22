package com.lomeu.votingapi.service;

import com.lomeu.votingapi.dto.response.VotingResultResponse;
import com.lomeu.votingapi.entity.VoteChoice;
import com.lomeu.votingapi.exception.BusinessException;
import com.lomeu.votingapi.exception.ErrorCode;
import com.lomeu.votingapi.repository.TopicRepository;
import com.lomeu.votingapi.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VotingResultService {

    private final TopicRepository topicRepository;
    private final VoteRepository voteRepository;

    public VotingResultService(TopicRepository topicRepository, VoteRepository voteRepository) {
        this.topicRepository = topicRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional(readOnly = true)
    public VotingResultResponse getResult(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new BusinessException(ErrorCode.TOPIC_NOT_FOUND);
        }

        return VotingResultResponse.from(
                topicId,
                voteRepository.aggregateByTopicId(topicId, VoteChoice.YES, VoteChoice.NO)
        );
    }
}
