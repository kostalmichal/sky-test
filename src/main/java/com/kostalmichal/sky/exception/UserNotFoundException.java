package com.kostalmichal.sky.exception;

import com.kostalmichal.sky.domain.types.UserId;

public class UserNotFoundException extends RuntimeException {

    private final UserId id;

    public UserNotFoundException(UserId id) {
        super("User " + id + " not found.");

        this.id = id;
    }

    public UserId getId() {
        return this.id;
    }
}
