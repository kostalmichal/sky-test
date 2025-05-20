package com.kostalmichal.sky.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class UserRequest {
    @NotNull
    @Schema(description = "A name of newly registered user.")
    public String name;

    @NotNull
    @Schema(description = "An email of newly registered user.")
    public String email;

    @NotNull
    @Schema(description = "A password for newly registered user.")
    public String password;

    public UserRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }
}
