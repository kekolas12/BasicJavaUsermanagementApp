package mertcan.usermanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.repository.UserRepository;
import mertcan.usermanagement.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
        
        return UserPrincipal.create(user);
    }
    
    @Transactional
    public UserDetails loadUserById(Long id) {
        log.debug("Loading user by ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + id));
        
        return UserPrincipal.create(user);
    }
}
