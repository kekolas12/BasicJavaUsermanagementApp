package mertcan.usermanagement.handler.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.exception.UserNotFoundException;
import mertcan.usermanagement.mediator.IRequestHandler;
import mertcan.usermanagement.query.user.GetUserByIdQuery;
import mertcan.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetUserByIdQueryHandler implements IRequestHandler<GetUserByIdQuery, UserDto> {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDto handle(GetUserByIdQuery query) {
        log.info("Getting user by ID: {}", query.getId());
        
        User user = userRepository.findById(query.getId())
            .orElseThrow(() -> new UserNotFoundException(query.getId()));
        
        return convertToDto(user);
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
