package com.lomeu.votingapi.repository;

import com.lomeu.votingapi.entity.Vote;
import com.lomeu.votingapi.entity.VoteChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByTopicIdAndMemberId(Long topicId, String memberId);

    @Query("""
            SELECT COUNT(v) AS totalVotes,
                   COALESCE(SUM(CASE WHEN v.vote = :yesVote THEN 1 ELSE 0 END), 0) AS yesVotes,
                   COALESCE(SUM(CASE WHEN v.vote = :noVote THEN 1 ELSE 0 END), 0) AS noVotes
            FROM Vote v
            WHERE v.topic.id = :topicId
            """)
    VoteCountProjection aggregateByTopicId(
            @Param("topicId") Long topicId,
            @Param("yesVote") VoteChoice yesVote,
            @Param("noVote") VoteChoice noVote
    );
}
