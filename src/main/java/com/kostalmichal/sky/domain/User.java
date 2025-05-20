package com.kostalmichal.sky.domain;

import com.kostalmichal.sky.domain.types.Email;
import com.kostalmichal.sky.domain.types.ExternalProjectId;
import com.kostalmichal.sky.domain.types.UserId;
import jakarta.persistence.*;

import java.util.*;

@Entity(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //Email for user
    @Column(nullable = false)
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false, unique = true))
    })
    private Email email;

    //Name for user
    @Column(nullable = false)
    private String name;

    //Password for user
    @Column(nullable = false)
    private String password;

    /*
    Role of an user>
    ROLE_ADMIN - admin role
    ROLE_USER - user role
     */
    @Column(nullable = false)
    private String role = "ROLE_USER";

    //List of external projects
    @OneToMany(mappedBy = "userId")
    private final Set<ExternalProject> externalProjects = new HashSet<ExternalProject>();

    protected User() {

    }

    public User(UserId id, Email email, String name, String password, String role) {
        this(email, name, password, role);
        this.id = id.getValue();
    }

    public User(Email email, String name, String password, String role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public UserId getId() {
        return new UserId(id);
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeEmail(Email email) {
        this.email = email;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public ExternalProject addExternalProject(String name) {
        ExternalProject externalProject = new ExternalProject(name, this.getId());
        externalProjects.add(externalProject);
        return externalProject;
    }

    public Set<ExternalProject> getExternalProjects() {
        return externalProjects;
    }

    public ExternalProject getExternalProject(ExternalProjectId externalProjectId) {
        return externalProjects.stream().filter(p -> p.getId() == externalProjectId).findAny().get();
    }

    @Override
    public String toString() {
        return "User{" +
                "Id=" + id +
                ", email=" + email +
                ", name='" + name + '\'' +
                ", externalProjects=" + externalProjects +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(name, user.name) && Objects.equals(password, user.password) && Objects.equals(externalProjects, user.externalProjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name, password, externalProjects);
    }
}