package com.kostalmichal.sky.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class ExternalProjectRequest {
    @NotNull
    @Schema(description = "A name of an external project a user is working on.")
    public String name;

    public ExternalProjectRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
