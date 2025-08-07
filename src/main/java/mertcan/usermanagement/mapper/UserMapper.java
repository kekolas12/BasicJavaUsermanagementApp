package mertcan.usermanagement.mapper;

import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {
    
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setIsActive(user.getIsActive());
        dto.setIsEmailVerified(user.getIsEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        
        if (user.getRoles() != null) {
            dto.setRoleNames(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
}
