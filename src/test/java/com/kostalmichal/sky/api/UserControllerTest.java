package com.kostalmichal.sky.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.kostalmichal.sky.api.dto.ExternalProjectRequest;
import com.kostalmichal.sky.api.dto.UserRequest;
import com.kostalmichal.sky.domain.ExternalProject;
import com.kostalmichal.sky.domain.User;
import com.kostalmichal.sky.domain.types.Email;
import com.kostalmichal.sky.domain.types.ExternalProjectId;
import com.kostalmichal.sky.domain.types.UserId;
import com.kostalmichal.sky.repository.UserRepository;
import com.kostalmichal.sky.security.config.TestSecurityConfig;
import com.kostalmichal.sky.security.service.DatabaseUserDetailsService;
import com.kostalmichal.sky.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getUser_ShouldReturnUser() throws Exception {
        User user = createTestUser(new UserId(1L));

        when(userService.getUser(eq(user.getId()))).thenReturn(user);

        mockMvc.perform(get("/v1/users/{userId}", user.getId().getValue())
                        .with(httpBasic("user", "none"))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId().getValue().intValue())))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$._links.self.href", containsString("/v1/users/" + user.getId().getValue())))
                .andExpect(jsonPath("$._links.externalProjects.href", containsString("/v1/users/" + user.getId().getValue() + "/external-projects")))
                .andExpect(jsonPath("$._links.update.href", containsString("/v1/users/" + user.getId().getValue())))
                .andExpect(jsonPath("$._links.delete.href", containsString("/v1/users/" + user.getId().getValue())));
    }

    @Test
    void getExternalProject_ShouldReturnProject() throws Exception {
        ExternalProject project = createTestExternalProject(new ExternalProjectId(10L), new UserId(1L));

        when(userService.getExternalProject(eq(project.getUserId()), eq(project.getId()))).thenReturn(project);

        mockMvc.perform(get("/v1/users/{userId}/external-projects/{externalProjectId}", project.getUserId().getValue(), project.getId().getValue())
                .with(httpBasic("user", "none"))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(project.getId().getValue().intValue())))
                .andExpect(jsonPath("$.name", is("Test Project")))
                .andExpect(jsonPath("$.userId", is(project.getUserId().getValue().intValue())))
                .andExpect(jsonPath("$._links.self.href", containsString("/v1/users/" + project.getUserId().getValue() + "/external-projects/" + project.getId().getValue())))
                .andExpect(jsonPath("$._links.user.href", containsString("/v1/users/" + project.getUserId().getValue())))
                .andExpect(jsonPath("$._links.update.href", containsString("/v1/users/" + project.getUserId().getValue() + "/external-projects/" + project.getId().getValue())))
                .andExpect(jsonPath("$._links.delete.href", containsString("/v1/users/" + project.getUserId().getValue() + "/external-projects/" + project.getId().getValue())));
    }

    @Test
    void getExternalProjects_ShouldReturnCollection() throws Exception {
        Long userId = 1L;
        UserId userIdObj = new UserId(userId);
        User user = createTestUser(userIdObj);

        Set<ExternalProject> projects = new HashSet<>();
        ExternalProject project1 = createTestExternalProject(new ExternalProjectId(10L), userIdObj);
        ExternalProject project2 = createTestExternalProject(new ExternalProjectId(11L), userIdObj);
        projects.add(project1);
        projects.add(project2);

        when(userService.getExternalProjects(eq(userIdObj))).thenReturn(projects);

        mockMvc.perform(get("/v1/users/{userId}/external-projects", userId)
                        .with(httpBasic("user", "none"))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.externalProjectResponseList", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href", containsString("/v1/users/" + userId + "/external-projects")))
                .andExpect(jsonPath("$._links.user.href", containsString("/v1/users/" + userId)))
                .andExpect(jsonPath("$._embedded.externalProjectResponseList[?(@.id == 10)]._links.self.href",
                        hasItem(containsString("/v1/users/" + userId + "/external-projects/10"))))
                .andExpect(jsonPath("$._embedded.externalProjectResponseList[?(@.id==10)]._links.update.href",
                        hasItem(containsString("/v1/users/" + userId + "/external-projects/10"))))
                .andExpect(jsonPath("$._embedded.externalProjectResponseList[?(@.id==11)]._links.self.href",
                        hasItem(containsString("/v1/users/" + userId + "/external-projects/11"))));
    }

    @Test
    void createUser_ShouldBeForbidden() throws Exception {
        Long userId = 1L;
        UserId userIdObj = new UserId(userId);
        User user = createTestUser(userIdObj);
        UserRequest request = new UserRequest("Test User", "test@example.com", "password");

        when(userService.registerNewUser(eq(new Email("test@example.com")), eq("Test User"), any())).thenReturn(user);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "none"))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createUser_ShouldReturnUser() throws Exception {
        Long userId = 1L;
        UserId userIdObj = new UserId(userId);
        User user = createTestUser(userIdObj);
        UserRequest request = new UserRequest("Test User", "test@example.com", "password");

        when(userService.registerNewUser(eq(new Email("test@example.com")), eq("Test User"), any())).thenReturn(user);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("admin", "none"))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/v1/users/" + userId)))
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$._links.self.href", containsString("/v1/users/" + userId)))
                .andExpect(jsonPath("$._links.externalProjects.href", containsString("/v1/users/" + userId + "/external-projects")))
                .andExpect(jsonPath("$._links.update.href", containsString("/v1/users/" + userId)))
                .andExpect(jsonPath("$._links.delete.href", containsString("/v1/users/" + userId)));
    }

    @Test
    void createExternalProject_ShouldReturnProject() throws Exception {
        Long userId = 1L;
        UserId userIdObj = new UserId(userId);
        Long projectId = 10L;
        ExternalProjectId projectIdObj = new ExternalProjectId(projectId);

        ExternalProject project = createTestExternalProject(projectIdObj, userIdObj);
        ExternalProjectRequest request = new ExternalProjectRequest("Test Project");

        when(userService.addExternalProject(eq(userIdObj), eq("Test Project"))).thenReturn(project);

        mockMvc.perform(post("/v1/users/{userId}/external-projects", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(httpBasic("admin", "none")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/v1/users/" + userId + "/external-projects/" + projectId)))
                .andExpect(jsonPath("$.id", is(projectId.intValue())))
                .andExpect(jsonPath("$.name", is("Test Project")))
                .andExpect(jsonPath("$.userId", is(userId.intValue())))
                // Test HATEOAS links
                .andExpect(jsonPath("$._links.self.href", containsString("/v1/users/" + userId + "/external-projects/" + projectId)))
                .andExpect(jsonPath("$._links.user.href", containsString("/v1/users/" + userId)))
                .andExpect(jsonPath("$._links.update.href", containsString("/v1/users/" + userId + "/external-projects/" + projectId)))
                .andExpect(jsonPath("$._links.delete.href", containsString("/v1/users/" + userId + "/external-projects/" + projectId)));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        Long userId = 1L;
        UserId userIdObj = new UserId(userId);
        User user = createTestUser(userIdObj);
        UserRequest request = new UserRequest("Updated User", "udpated@example.com", "password");

        when(userService.updateUser(eq(userIdObj), eq("Updated User"), any())).thenReturn(
                createUpdatedTestUser(userIdObj, "Updated User", "updated@example.com")
        );

        mockMvc.perform(put("/v1/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("admin", "none"))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.name", is("Updated User")))
                .andExpect(jsonPath("$._links.self.href", containsString("/v1/users/" + userId)))
                .andExpect(jsonPath("$._links.externalProjects.href", containsString("/v1/users/" + userId + "/external-projects")))
                .andExpect(jsonPath("$._links.update.href", containsString("/v1/users/" + userId)))
                .andExpect(jsonPath("$._links.delete.href", containsString("/v1/users/" + userId)));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/v1/users/{userId}", userId)
                    .with(httpBasic("admin", "none"))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteExternalProject_ShouldReturnNoContent() throws Exception {
        Long userId = 1L;
        Long projectId = 10L;

        // When & Then
        mockMvc.perform(delete("/v1/users/{userId}/external-projects/{externalProjectId}", userId, projectId)
                        .with(httpBasic("admin", "none"))
                )
                .andExpect(status().isNoContent());
    }

    private User createTestUser(UserId userId) {
        User user = new User(new Email("test@example.com"), "Test User", "password", "ROLE_ADMIN");
        setUserId(user, userId);
        return user;
    }

    private User createUpdatedTestUser(UserId userId, String name, String email) {
        User user = new User(new Email(email), name, "password", "ROLE_ADMIN");
        setUserId(user, userId);
        return user;
    }

    private ExternalProject createTestExternalProject(ExternalProjectId projectId, UserId userId) {
        ExternalProject project = new ExternalProject("Test Project", userId);
        setProjectId(project, projectId);
        return project;
    }

    private void setUserId(User user, UserId userId) {
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, userId.getValue());
        } catch (Exception e) {
            throw new RuntimeException("Could not set user ID via reflection", e);
        }
    }

    private void setProjectId(ExternalProject project, ExternalProjectId projectId) {
        try {
            java.lang.reflect.Field idField = ExternalProject.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(project, projectId.getValue());
        } catch (Exception e) {
            throw new RuntimeException("Could not set project ID via reflection", e);
        }
    }
}
