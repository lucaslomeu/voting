package com.lomeu.votingapi.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred."),
    CPF_NOT_FOUND(HttpStatus.NOT_FOUND, "CPF_NOT_FOUND", "The CPF was not found."),
    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "TOPIC_NOT_FOUND", "The topic was not found."),
    VOTING_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "VOTING_SESSION_NOT_FOUND", "The voting session was not found."),
    VOTING_SESSION_ALREADY_EXISTS(HttpStatus.CONFLICT, "VOTING_SESSION_ALREADY_EXISTS", "The topic already has a voting session."),
    VOTING_SESSION_CLOSED(HttpStatus.CONFLICT, "VOTING_SESSION_CLOSED", "The voting session is closed."),
    DUPLICATE_VOTE(HttpStatus.CONFLICT, "DUPLICATE_VOTE", "The member has already voted on this topic."),
    INVALID_VOTE(HttpStatus.BAD_REQUEST, "INVALID_VOTE", "Vote must be YES or NO."),
    INVALID_DURATION(HttpStatus.BAD_REQUEST, "INVALID_DURATION", "Duration must be greater than zero.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
