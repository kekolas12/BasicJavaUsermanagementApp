package mertcan.usermanagement.handler.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.command.user.UpdateUserCommand;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.entity.Role;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.exception.BusinessException;
import mertcan.usermanagement.exception.UserNotFoundException;
import mertcan.usermanagement.mediator.IRequestHandler;
import mertcan.usermanagement.repository.RoleRepository;
import mertcan.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserCommandHandler implements IRequestHandler<UpdateUserCommand, UserDto> {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @Override
    @Transactional
    public UserDto handle(UpdateUserCommand command) {
        log.info("Updating user with ID: {}", command.getId());
        
        User user = userRepository.findById(command.getId())
            .orElseThrow(() -> new UserNotFoundException(command.getId()));
        
        // Email kontrolü (eğer değiştiriliyorsa)
        if (command.getEmail() != null && !command.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(command.getEmail())) {
                throw new BusinessException("Bu email adresi zaten kullanılmaktadır: " + command.getEmail());
            }
            user.setEmail(command.getEmail());
        }
        

        if (command.getFirstName() != null) {
            user.setFirstName(command.getFirstName());
        }
        
        if (command.getLastName() != null) {
            user.setLastName(command.getLastName());
        }
        
        if (command.getPhoneNumber() != null) {
            user.setPhoneNumber(command.getPhoneNumber());
        }
        
        if (command.getIsActive() != null) {
            user.setIsActive(command.getIsActive());
        }
        
        // Rolleri güncelle
        if (command.getRoleNames() != null) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : command.getRoleNames()) {
                Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new BusinessException("Rol bulunamadı: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }
        
        // Kullanıcıyı kaydet
        User savedUser = userRepository.save(user);
        
        log.info("User updated successfully with ID: {}", savedUser.getId());
        
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
