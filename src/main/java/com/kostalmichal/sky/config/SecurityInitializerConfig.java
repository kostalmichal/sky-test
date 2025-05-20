package com.kostalmichal.sky.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rozšíření inicializační konfigurace pro security integraci
 */
@Configuration
public class SecurityInitializerConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityInitializerConfig.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Výpis hashů hesel pro ověření přihlášení
     * Pouze pro vývojové a testovací profily
     */
    @Bean
    @Profile({"dev", "test"})
    public CommandLineRunner logPasswordHashes() {
        return args -> {
            // Pouze pro účely vývoje - vypíše zakódovaná hesla do logu
            // V produkci NIKDY nepoužívejte pevně daná hesla v kódu
            logger.info("Hesla pro testovací účty:");
            logger.info("admin: {}", passwordEncoder.encode("admin"));
            logger.info("user: {}", passwordEncoder.encode("password"));
            logger.info("Poznámka: Při každém spuštění se generují nové hashe, ale Spring Security je umí porovnat");
        };
    }
}
