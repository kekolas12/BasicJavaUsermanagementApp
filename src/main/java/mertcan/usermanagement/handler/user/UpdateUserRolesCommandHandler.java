package mertcan.usermanagement.handler.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.command.user.UpdateUserRolesCommand;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.entity.Role;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.exception.BusinessException;
import mertcan.usermanagement.mapper.UserMapper;
import mertcan.usermanagement.mediator.IRequestHandler;
import mertcan.usermanagement.repository.RoleRepository;
import mertcan.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserRolesCommandHandler implements IRequestHandler<UpdateUserRolesCommand, UserDto> {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public UserDto handle(UpdateUserRolesCommand request) {
        log.info("Updating roles for user ID: {}", request.getUserId());
        
        // Find the user
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new BusinessException("User not found with ID: " + request.getUserId()));
        
        // Find all requested roles
        Set<Role> roles = new HashSet<>();
        for (String roleName : request.getRoleNames()) {
            Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BusinessException("Role not found: " + roleName));
            roles.add(role);
        }
        
        // Update user roles
        user.setRoles(roles);
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        log.info("Successfully updated roles for user: {} with roles: {}", 
            user.getUsername(), request.getRoleNames());
        
        return userMapper.toDto(savedUser);
    }
}
