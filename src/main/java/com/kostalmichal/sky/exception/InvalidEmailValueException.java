package com.kostalmichal.sky.exception;

public class InvalidEmailValueException extends RuntimeException {

    public InvalidEmailValueException(String value) {
        super("Invalid email value '" + value + "'.");
    }
}
