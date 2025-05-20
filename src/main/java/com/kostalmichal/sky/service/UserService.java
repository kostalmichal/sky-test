package com.kostalmichal.sky.service;

import com.kostalmichal.sky.domain.ExternalProject;
import com.kostalmichal.sky.domain.User;
import com.kostalmichal.sky.domain.types.Email;
import com.kostalmichal.sky.domain.types.ExternalProjectId;
import com.kostalmichal.sky.domain.types.UserId;

import java.util.Set;

public interface UserService {

    public User getUser(UserId userId);

    public User registerNewUser(Email email, String name, String password, String role);

    public User registerNewUser(Email email, String name, String password);

    public ExternalProject getExternalProject(UserId userId, ExternalProjectId externalProjectId);

    public Set<ExternalProject> getExternalProjects(UserId userId);

    public ExternalProject addExternalProject(UserId userId, String externalProjectName);

    public ExternalProject updateExternalProject(UserId userId, ExternalProjectId externalProjectId, String externalProjectName);

    public void deleteExternalProject(UserId userId, ExternalProjectId externalProjectId);

    public User updateUser(UserId userId, String name, Email email);

    public User changePassword(UserId userId, String password);

    public void deleteUser(UserId userId);
}
