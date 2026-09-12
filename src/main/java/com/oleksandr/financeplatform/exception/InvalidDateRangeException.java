package com.oleksandr.financeplatform.exception;

public class InvalidDateRangeException extends RuntimeException {

    public InvalidDateRangeException() {
        super("The from date must be on or before the to date");
    }
}
