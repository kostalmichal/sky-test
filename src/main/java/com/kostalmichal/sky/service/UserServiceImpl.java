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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ExternalProjectRepository externalProjectRepository;
    private final PasswordEncoder passwordEncoder;

    UserServiceImpl(UserRepository userRepository, ExternalProjectRepository externalProjectRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.externalProjectRepository = externalProjectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUser(UserId id) {
        return userRepository.findById(id.getValue()).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public ExternalProject getExternalProject(UserId userId, ExternalProjectId externalProjectId) {
        ExternalProject externalProject = externalProjectRepository.findById(externalProjectId.getValue()).orElseThrow(() -> new ExternalProjectNotFoundException(userId, externalProjectId));;

        if (!Objects.equals(externalProject.getUserId(), userId)) {
            throw new ExternalProjectNotFoundException(userId, externalProjectId);
        }

        return externalProject;
    }

    @Override
    public Set<ExternalProject> getExternalProjects(UserId userId) {
        User user = userRepository.findById(userId.getValue()).orElseThrow(() -> new UserNotFoundException(userId));

        return user.getExternalProjects();
    }

    @Override
    @Transactional
    public User registerNewUser(Email email, String name, String password) {
        return userRepository.save(new User(email, name, passwordEncoder.encode(password), "ROLE_USER"));
    }

    @Override
    @Transactional
    public User registerNewUser(Email email, String name, String password, String role) {
        return userRepository.save(new User(email, name, passwordEncoder.encode(password), role));
    }

    @Override
    @Transactional
    public ExternalProject addExternalProject(UserId userId, String externalProjectName) {
        User user = userRepository.findById(userId.getValue()).orElseThrow(() -> new UserNotFoundException(userId));
        ExternalProject externalProject = user.addExternalProject(externalProjectName);
        externalProjectRepository.save(externalProject);
        return externalProject;
    }

    @Override
    @Transactional
    public ExternalProject updateExternalProject(UserId userId, ExternalProjectId externalProjectId, String externalProjectName) {
        ExternalProject externalProject = externalProjectRepository.findById(externalProjectId.getValue()).orElseThrow(() -> new ExternalProjectNotFoundException(userId, externalProjectId));

        if (!Objects.equals(externalProject.getUserId(), userId)) {
            throw new ExternalProjectNotFoundException(userId, externalProjectId);
        }
        externalProject.changeName(externalProjectName);
        externalProjectRepository.save(externalProject);

        return externalProject;
    }

    @Override
    @Transactional
    public void deleteExternalProject(UserId userId, ExternalProjectId externalProjectId) {
        ExternalProject externalProject = externalProjectRepository.findById(externalProjectId.getValue()).orElseThrow(() -> new ExternalProjectNotFoundException(userId, externalProjectId));

        if (!Objects.equals(externalProject.getUserId(), userId)) {
            throw new ExternalProjectNotFoundException(userId, externalProjectId);
        }
        externalProjectRepository.delete(externalProject);
    }

    @Override
    @Transactional
    public User updateUser(UserId userId, String name, Email email) {
        User user = userRepository.findById(userId.getValue()).orElseThrow(() -> new UserNotFoundException(userId));
        user.changeName(name);
        user.changeEmail(email);
        user = userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public User changePassword(UserId userId, String password) {
        User user = userRepository.findById(userId.getValue()).orElseThrow(() -> new UserNotFoundException(userId));
        user.changePassword(passwordEncoder.encode(password)); // Zakódování hesla
        user = userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(UserId userId) {
        User user = userRepository.findById(userId.getValue()).orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
    }
}