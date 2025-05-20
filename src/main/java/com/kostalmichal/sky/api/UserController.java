package com.kostalmichal.sky.api;

import com.kostalmichal.sky.api.dto.ExternalProjectRequest;
import com.kostalmichal.sky.api.dto.ExternalProjectResponse;
import com.kostalmichal.sky.api.dto.UserRequest;
import com.kostalmichal.sky.api.dto.UserResponse;
import com.kostalmichal.sky.domain.ExternalProject;
import com.kostalmichal.sky.domain.User;
import com.kostalmichal.sky.domain.types.Email;
import com.kostalmichal.sky.domain.types.ExternalProjectId;
import com.kostalmichal.sky.domain.types.UserId;
import com.kostalmichal.sky.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/v1/", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Users", description = "Endpoints to access the users.")
// API responses that might be returned by all the methods in this class
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))})
})
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value="/users/{userId}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        User user = userService.getUser(new UserId(userId));

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(new UserResponse(user));
    }

    @GetMapping(value = "/users/{userId}/external-projects/{externalProjectId}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<ExternalProjectResponse> getExternalProject(@PathVariable Long userId, @PathVariable Long externalProjectId) {
        ExternalProject externalProject = userService.getExternalProject(new UserId(userId), new ExternalProjectId(externalProjectId));

        return ResponseEntity.status(HttpStatus.OK).body(new ExternalProjectResponse(externalProject));
    }

    @GetMapping(value = "/users/{userId}/external-projects")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<CollectionModel<ExternalProjectResponse>> getExternalProjects(@PathVariable Long userId) {
        Set<ExternalProject> externalProjects = userService.getExternalProjects(new UserId(userId));

        // Vytvoření kolekčního modelu s odkazy na úrovni kolekce
        Link selfLink = linkTo(methodOn(UserController.class).getExternalProjects(userId)).withSelfRel();
        Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");

        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(externalProjects.stream().map(ExternalProjectResponse::new).collect(Collectors.toSet()), selfLink, userLink));
    }

    @PostMapping(value = "/users")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<UserResponse> registerNewUser(@Valid @RequestBody final UserRequest request) {
        User newUser = userService.registerNewUser(new Email(request.getEmail()), request.getName(), request.getPassword());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUser.getId().getValue())
                .toUri();

        return ResponseEntity.created(location).body(new UserResponse(newUser));
    }

    @PostMapping(value = "/users/{userId}/external-projects")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<ExternalProjectResponse> addExternalProject(@PathVariable Long userId, @Valid @RequestBody final ExternalProjectRequest request) {
        ExternalProject externalProject = userService.addExternalProject(new UserId(userId), request.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(externalProject.getId().getValue())
                .toUri();

        return ResponseEntity.created(location).body(new ExternalProjectResponse(externalProject));
    }

    @PutMapping(value = "/users/{userId}/external-projects/{externalProjectId}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<ExternalProjectResponse> updateExternalProject(@PathVariable Long userId, @PathVariable Long externalProjectId, @Valid @RequestBody final ExternalProjectRequest request) {
        ExternalProject externalProject = userService.updateExternalProject(new UserId(userId), new ExternalProjectId(externalProjectId), request.getName());

        return ResponseEntity.status(HttpStatus.OK).body(new ExternalProjectResponse(externalProject));
    }

    @DeleteMapping(value = "/users/{userId}/external-projects/{externalProjectId}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Void> deleteExternalProject(@PathVariable Long userId, @PathVariable Long externalProjectId) {
        userService.deleteExternalProject(new UserId(userId), new ExternalProjectId(externalProjectId));

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/users/{userId}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @Valid @RequestBody final UserRequest request) {
        User user = userService.updateUser(new UserId(userId), request.getName(), new Email(request.getEmail()));

        return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
    }

    @DeleteMapping(value = "/users/{userId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(new UserId(userId));

        return ResponseEntity.noContent().build();
    }

}
