package mertcan.usermanagement.handler.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.mediator.IRequestHandler;
import mertcan.usermanagement.query.user.GetAllUsersQuery;
import mertcan.usermanagement.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetAllUsersQueryHandler implements IRequestHandler<GetAllUsersQuery, Page<UserDto>> {
    
    private final UserRepository userRepository;
    
    @Override
    public Page<UserDto> handle(GetAllUsersQuery query) {
        log.info("Getting all users with pagination - Page: {}, Size: {}", query.getPage(), query.getSize());
        
        // Sıralama
        Sort sort = Sort.by(query.getSortDir().equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC, query.getSortBy());
        
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), sort);
        
        Page<User> users;
        
        // Filtreleme
        if (query.getSearch() != null && !query.getSearch().trim().isEmpty()) {
            users = userRepository.findBySearchTerm(query.getSearch().trim(), pageable);
        } else if (query.getIsActive() != null) {
            users = userRepository.findByIsActive(query.getIsActive(), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        
        return users.map(this::convertToDto);
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
