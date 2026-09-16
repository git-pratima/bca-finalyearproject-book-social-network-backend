package com.bca.pratima;

import com.bca.pratima.entity.Role;
import com.bca.pratima.repository.RoleRepository;
import com.bca.pratima.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
@SpringBootApplication
public class BcaFinalYearProjectApplication {



	public static void main(String[] args) {
		SpringApplication.run(BcaFinalYearProjectApplication.class, args);
	}

    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            var normalRole = roleRepository.findByName("NORMAL")
                    .orElseGet(() -> roleRepository.findByName("USER")
                            .map(legacyRole -> {
                                legacyRole.setName("NORMAL");
                                return roleRepository.save(legacyRole);
                            })
                            .orElseGet(() -> roleRepository.save(Role.builder().name("NORMAL").build())));

            // Handles databases where both roles already exist by moving all
            // legacy USER assignments to NORMAL before the old role is removed.
            roleRepository.findByName("USER").ifPresent(legacyRole -> {
                userRepository.findAll().stream()
                        .filter(user -> user.getRoles().stream()
                                .anyMatch(role -> role.getId().equals(legacyRole.getId())))
                        .forEach(user -> {
                            user.setRoles(java.util.List.of(normalRole));
                            userRepository.save(user);
                        });
                roleRepository.delete(legacyRole);
            });
        };
    }

}
