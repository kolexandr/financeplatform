package com.oleksandr.financeplatform.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("Email address is already registered");
    }
}
