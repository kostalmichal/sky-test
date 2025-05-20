package com.kostalmichal.sky.domain;

import com.kostalmichal.sky.domain.types.ExternalProjectId;
import com.kostalmichal.sky.domain.types.UserId;
import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "tb_user_external_project")
public class ExternalProject {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    //Name of the external project
    public String name;

    //User that owns the external project
    //@ManyToOne
    @JoinColumn(name = "user_id")
    private Long userId;

    protected ExternalProject() {

    }

    public ExternalProject(String name, UserId userId) {
        this.name = name;
        this.userId = userId.getValue();
    }

    public ExternalProjectId getId() {
        return new ExternalProjectId(id);
    }

    public String getName() {
        return this.name;
    }

    public UserId getUserId() {
        return new UserId(this.userId);
    }

    public String changeName(String name) {
        this.name = name;

        return this.name;
    }

    @Override
    public String toString() {
        return "ExternalProject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExternalProject that = (ExternalProject) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
