package com.lomeu.votingapi.exception;

public final class ConstraintViolationDetector {

    private ConstraintViolationDetector() {
    }

    public static boolean contains(Throwable exception, String constraintName) {
        Throwable current = exception;
        while (current != null) {
            if (current.getMessage() != null && current.getMessage().contains(constraintName)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
