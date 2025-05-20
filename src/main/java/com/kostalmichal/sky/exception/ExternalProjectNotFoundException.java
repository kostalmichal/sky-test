package com.kostalmichal.sky.exception;

import com.kostalmichal.sky.domain.types.ExternalProjectId;
import com.kostalmichal.sky.domain.types.UserId;

public class ExternalProjectNotFoundException extends RuntimeException {

    private final UserId userId;
    private final ExternalProjectId externalProjectId;

    public ExternalProjectNotFoundException(UserId userId, ExternalProjectId externalProjectId) {
        super("External project " + externalProjectId + " for user " + userId + " not found.");

        this.userId = userId;
        this.externalProjectId = externalProjectId;
    }

    public UserId getUserId() {
        return userId;
    }

    public ExternalProjectId getExternalProjectId() {
        return externalProjectId;
    }
}
