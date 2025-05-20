package com.kostalmichal.sky.api.dto;

import com.kostalmichal.sky.api.UserController;
import com.kostalmichal.sky.domain.ExternalProject;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ExternalProjectResponse extends RepresentationModel<ExternalProjectResponse> {
    private final Long id;
    private final String name;
    private final Long userId;

    public ExternalProjectResponse(ExternalProject externalProject) {
        this.id = externalProject.getId().getValue();
        this.name = externalProject.getName();
        this.userId = externalProject.getUserId().getValue();

        add(
                linkTo(methodOn(UserController.class).getExternalProject(this.userId, this.id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getUser(this.userId)).withRel("user"),
                linkTo(methodOn(UserController.class).updateExternalProject(this.userId, this.id, null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteExternalProject(this.userId, this.id)).withRel("delete")
        );
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Long getUserId() {
        return this.userId;
    }

}
