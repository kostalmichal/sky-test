package com.kostalmichal.sky.repository;

import com.kostalmichal.sky.domain.ExternalProject;
import com.kostalmichal.sky.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface ExternalProjectRepository extends CrudRepository<ExternalProject, Long> {

    @NonNull
    Optional<ExternalProject> findById(@NonNull Long id);
}
