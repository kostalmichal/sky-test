package com.kostalmichal.sky.api.dto;

import com.kostalmichal.sky.api.UserController;
import com.kostalmichal.sky.domain.User;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class UserResponse extends RepresentationModel<UserResponse> {
    private final Long id;
    private final String email;
    private final String name;

    public UserResponse(User user) {
        this.email = user.getEmail().getValue();
        this.name = user.getName();
        this.id = user.getId().getValue();

        add(
                linkTo(methodOn(UserController.class).getUser(this.id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getExternalProjects(this.id)).withRel("externalProjects"),
                linkTo(methodOn(UserController.class).updateUser(this.id, null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(this.id)).withRel("delete")
        );

    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}