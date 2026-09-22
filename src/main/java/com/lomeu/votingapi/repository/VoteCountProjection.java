package com.lomeu.votingapi.repository;

public interface VoteCountProjection {

    Long getTotalVotes();

    Long getYesVotes();

    Long getNoVotes();
}
