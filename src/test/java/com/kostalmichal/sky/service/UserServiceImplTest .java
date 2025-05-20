package com.kostalmichal.sky.service;

import com.kostalmichal.sky.domain.ExternalProject;
import com.kostalmichal.sky.domain.User;
import com.kostalmichal.sky.domain.types.Email;
import com.kostalmichal.sky.domain.types.ExternalProjectId;
import com.kostalmichal.sky.domain.types.UserId;
import com.kostalmichal.sky.exception.ExternalProjectNotFoundException;
import com.kostalmichal.sky.exception.UserNotFoundException;
import com.kostalmichal.sky.repository.ExternalProjectRepository;
import com.kostalmichal.sky.repository.UserRepository;
import com.kostalmichal.sky.security.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExternalProjectRepository externalProjectRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, externalProjectRepository, passwordEncoder);
    }

    @Test
    void getUser_WhenUserExists_ShouldReturnUser() {
        UserId userId = new UserId(1L);
        User expectedUser = createTestUser(userId);

        when(userRepository.findById(userId.getValue())).thenReturn(Optional.of(expectedUser));

        User result = userService.getUser(userId);

        assertEquals(expectedUser, result);
    }

    @Test
    void getUser_WhenUserDoesNotExist_ShouldThrowException() {
        UserId userId = new UserId(1L);

        when(userRepository.findById(userId.getValue())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void getExternalProject_WhenProjectExistsAndBelongsToUser_ShouldReturnProject() {
        UserId userId = new UserId(1L);
        ExternalProjectId projectId = new ExternalProjectId(10L);

        ExternalProject expectedProject = createTestExternalProject(projectId, userId);

        when(externalProjectRepository.findById(projectId.getValue())).thenReturn(Optional.of(expectedProject));

        ExternalProject result = userService.getExternalProject(userId, projectId);

        assertEquals(expectedProject, result);
    }

    @Test
    void getExternalProject_WhenProjectDoesNotExist_ShouldThrowException() {
        UserId userId = new UserId(1L);
        ExternalProjectId projectId = new ExternalProjectId(10L);

        when(externalProjectRepository.findById(projectId.getValue())).thenReturn(Optional.empty());

        assertThrows(ExternalProjectNotFoundException.class, () -> userService.getExternalProject(userId, projectId));
        verify(externalProjectRepository).findById(projectId.getValue());
    }

    @Test
    void getExternalProject_WhenProjectExistsButDoesNotBelongToUser_ShouldThrowException() {
        UserId userId = new UserId(1L);
        ExternalProjectId projectId = new ExternalProjectId(10L);
        UserId differentUserId = new UserId(2L);

        ExternalProject project = createTestExternalProject(projectId, differentUserId);

        when(externalProjectRepository.findById(projectId.getValue())).thenReturn(Optional.of(project));

        assertThrows(ExternalProjectNotFoundException.class, () -> userService.getExternalProject(userId, projectId));
    }

    @Test
    void getExternalProjects_WhenUserExists_ShouldReturnProjects() {
        UserId userId = new UserId(1L);

        Set<ExternalProject> expectedProjects = new HashSet<>();
        expectedProjects.add(createTestExternalProject(new ExternalProjectId(10L), userId));
        expectedProjects.add(createTestExternalProject(new ExternalProjectId(11L), userId));

        User user = createTestUser(userId);
        setExternalProjects(user, expectedProjects);

        when(userRepository.findById(userId.getValue())).thenReturn(Optional.of(user));

        Set<ExternalProject> result = userService.getExternalProjects(userId);

        assertEquals(expectedProjects, result);
        verify(userRepository).findById(userId.getValue());
    }

    @Test
    void registerNewUser_ShouldEncodePasswordAndSaveUser() {
        String email = "test@example.com";
        String name = "Test User";
        String password = "password";
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerNewUser(new Email(email), name, password);

        assertNotNull(result);
        assertEquals(email, result.getEmail().getValue());
        assertEquals(name, result.getName());

        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));

        assertEquals("ROLE_USER", result.getRole());
    }

    @Test
    void registerNewUserWithRole_ShouldEncodePasswordAndSaveUserWithSpecifiedRole() {
        String email = "admin@example.com";
        String name = "Admin User";
        String password = "adminPassword";
        String encodedPassword = "encodedAdminPassword";
        String role = "ROLE_ADMIN";

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerNewUser(new Email(email), name, password, role);

        assertNotNull(result);
        assertEquals(email, result.getEmail().getValue());
        assertEquals(name, result.getName());
        assertEquals(role, result.getRole());

        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addExternalProject_WhenUserExists_ShouldAddProjectAndSave() {
        UserId userId = new UserId(1L);
        String projectName = "Test Project";

        User user = createTestUser(userId);
        when(userRepository.findById(userId.getValue())).thenReturn(Optional.of(user));
        when(externalProjectRepository.save(any(ExternalProject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExternalProject result = userService.addExternalProject(userId, projectName);

        assertNotNull(result);
        assertEquals(projectName, result.getName());
        assertEquals(userId, result.getUserId());

        verify(userRepository).findById(userId.getValue());
        verify(externalProjectRepository).save(any(ExternalProject.class));
    }

    @Test
    void updateExternalProject_WhenProjectExistsAndBelongsToUser_ShouldUpdateAndSave() {
        UserId userId = new UserId(1L);
        ExternalProjectId projectId = new ExternalProjectId(10L);

        String oldName = "Old Project Name";
        String newName = "New Project Name";

        ExternalProject project = createTestExternalProject(projectId, userId);
        project.changeName(oldName);

        when(externalProjectRepository.findById(projectId.getValue())).thenReturn(Optional.of(project));
        when(externalProjectRepository.save(any(ExternalProject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExternalProject result = userService.updateExternalProject(userId, projectId, newName);

        assertNotNull(result);
        assertEquals(newName, result.getName());
    }

    @Test
    void deleteExternalProject_WhenProjectExistsAndBelongsToUser_ShouldDelete() {
        UserId userId = new UserId(1L);
        ExternalProjectId projectId = new ExternalProjectId(10L);

        ExternalProject project = createTestExternalProject(projectId, userId);

        when(externalProjectRepository.findById(projectId.getValue())).thenReturn(Optional.of(project));

        userService.deleteExternalProject(userId, projectId);

        verify(externalProjectRepository).findById(projectId.getValue());
        verify(externalProjectRepository).delete(project);
    }

    @Test
    void updateUser_ShouldUpdateAndSave() {
        UserId userId = new UserId(1L);

        String oldName = "Old Name";
        String newName = "New Name";
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";

        User user = createTestUser(userId);
        user.changeName(oldName);
        user.changeEmail(new Email(oldEmail));

        when(userRepository.findById(userId.getValue())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(userId, newName, new Email(newEmail));

        assertNotNull(result);
        assertEquals(newName, result.getName());
        assertEquals(newEmail, result.getEmail().getValue());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDelete() {
        UserId userId = new UserId(1L);

        User user = createTestUser(userId);

        when(userRepository.findById(userId.getValue())).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).findById(userId.getValue());
        verify(userRepository).delete(user);
    }

    private User createTestUser(UserId userId) {
        User user = new User(new Email("test@example.com"), "Test User", "password", "ROLE_USER");
        setUserId(user, userId);
        setExternalProjects(user, new HashSet<>());
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

    private void setExternalProjects(User user, Set<ExternalProject> projects) {
        try {
            java.lang.reflect.Field projectsField = User.class.getDeclaredField("externalProjects");
            projectsField.setAccessible(true);
            projectsField.set(user, projects);
        } catch (Exception e) {
            throw new RuntimeException("Could not set externalProjects via reflection", e);
        }
    }
}
