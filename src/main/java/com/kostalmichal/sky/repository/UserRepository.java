package com.kostalmichal.sky.repository;

import com.kostalmichal.sky.domain.User;
import com.kostalmichal.sky.domain.types.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find the user by an email for authentication.
     */
    Optional<User> findByEmailValue(String email);

    @NonNull
    Optional<User> findById(@NonNull Long id);
}
