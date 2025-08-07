package mertcan.usermanagement.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.entity.Role;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.repository.RoleRepository;
import mertcan.usermanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createRolesIfNotExist();
        createAdminUserIfNotExist();
    }

    private void createRolesIfNotExist() {
        if (roleRepository.count() == 0) {
            log.info("Creating default roles...");
            
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("System Administrator with full access");
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("Regular user with basic access");
            roleRepository.save(userRole);

            Role moderatorRole = new Role();
            moderatorRole.setName("MODERATOR");
            moderatorRole.setDescription("User with moderation privileges");
            roleRepository.save(moderatorRole);

            log.info("Default roles created successfully.");
        }
    }

    private void createAdminUserIfNotExist() {
        // Always reset admin password and roles to ensure it's correct
        User existingAdmin = userRepository.findByUsername("admin").orElse(null);
        if (existingAdmin != null) {
            log.info("Resetting admin password and roles...");
            
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            
            existingAdmin.setPassword(passwordEncoder.encode("admin123"));
            existingAdmin.setRoles(Set.of(adminRole)); // Reset roles too!
            userRepository.save(existingAdmin);
            log.info("Admin password and roles reset successfully. Username: admin, Password: admin123");
            return;
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            log.info("Creating admin user...");
            
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@usermanagement.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setIsActive(true);
            admin.setIsEmailVerified(true);
            admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);
            log.info("Admin user created successfully. Username: admin, Password: admin123");
        }
    }
}
