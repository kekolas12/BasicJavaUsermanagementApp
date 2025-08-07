package mertcan.usermanagement.handler.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.command.user.CreateUserCommand;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.entity.Role;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.exception.BusinessException;
import mertcan.usermanagement.mediator.IRequestHandler;
import mertcan.usermanagement.repository.RoleRepository;
import mertcan.usermanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateUserCommandHandler implements IRequestHandler<CreateUserCommand, UserDto> {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public UserDto handle(CreateUserCommand command) {
        log.info("Creating user with username: {}", command.getUsername());
        
        // Username validation
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new BusinessException("This username is already taken: " + command.getUsername());
        }
        
        // Email validation
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new BusinessException("This email address is already in use: " + command.getEmail());
        }
        
        // Create user
        User user = new User();
        user.setUsername(command.getUsername());
        user.setEmail(command.getEmail());
        user.setPassword(passwordEncoder.encode(command.getPassword()));
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        user.setPhoneNumber(command.getPhoneNumber());
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        
        // Assign default USER role
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new BusinessException("USER role not found"));
        roles.add(userRole);
        user.setRoles(roles);
        
        // Save user
        User savedUser = userRepository.save(user);
        
        log.info("User created successfully with ID: {} and default USER role", savedUser.getId());
        
        return convertToDto(savedUser);
    }
    
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setIsActive(user.getIsActive());
        dto.setIsEmailVerified(user.getIsEmailVerified());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        Set<String> roleNames = new HashSet<>();
        user.getRoles().forEach(role -> roleNames.add(role.getName()));
        dto.setRoleNames(roleNames);
        
        return dto;
    }
}
