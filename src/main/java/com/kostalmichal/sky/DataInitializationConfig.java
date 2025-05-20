package com.kostalmichal.sky;

import com.kostalmichal.sky.domain.types.Email;
import com.kostalmichal.sky.domain.types.UserId;
import com.kostalmichal.sky.exception.UserNotFoundException;
import com.kostalmichal.sky.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class DataInitializationConfig implements ApplicationRunner {

    private final UserService userService;

    public DataInitializationConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            userService.getUser(new UserId(1L));
        } catch (UserNotFoundException e) {
            userService.registerNewUser(new Email("admin@admin.com"), "Admin User", "admin", "ROLE_ADMIN");
        }
    }
}
