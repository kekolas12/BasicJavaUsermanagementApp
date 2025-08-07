package mertcan.usermanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.dto.JwtAuthenticationResponse;
import mertcan.usermanagement.dto.LoginRequest;
import mertcan.usermanagement.dto.RefreshTokenRequest;
import mertcan.usermanagement.entity.RefreshToken;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.exception.BusinessException;
import mertcan.usermanagement.exception.ErrorResponse;
import mertcan.usermanagement.repository.UserRepository;
import mertcan.usermanagement.security.JwtTokenProvider;
import mertcan.usermanagement.security.UserPrincipal;
import mertcan.usermanagement.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Kimlik doğrulama ve yetkilendirme API'leri - Login, logout, token yenileme işlemleri")
public class AuthController {
    
    @Value("${jwt.expiration}")
    private long jwtExpirationInSeconds;
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(
            @Valid @RequestBody 
            @Parameter(description = "Kullanıcı giriş bilgileri", required = true)
            LoginRequest loginRequest,
            HttpServletRequest request) {
        
        log.info("Login attempt for username: {}", loginRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı"));
            
            // JWT token oluştur
            String jwt = tokenProvider.generateToken(authentication);
            
            // Refresh token oluştur
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            // Son giriş zamanını güncelle
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Kullanıcı bilgileri
            Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
            
            JwtAuthenticationResponse.UserInfo userInfo = new JwtAuthenticationResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles,
                user.getLastLoginAt()
            );
            
            JwtAuthenticationResponse response = new JwtAuthenticationResponse(
                jwt,
                refreshToken.getToken(),
                jwtExpirationInSeconds,
                userInfo
            );
            
            log.info("Login successful for username: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Başarısız giriş denemesi logla
            log.warn("Login failed for username: {}", loginRequest.getUsername());
            throw new BusinessException("Geçersiz kullanıcı adı veya şifre");
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(
            @Valid @RequestBody 
            @Parameter(description = "Refresh token bilgisi", required = true)
            RefreshTokenRequest request) {
        
        String requestRefreshToken = request.getRefreshToken();
        
        return refreshTokenService.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
                String token = tokenProvider.generateTokenFromUser(user);
                
                Set<String> roles = user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toSet());
                
                JwtAuthenticationResponse.UserInfo userInfo = new JwtAuthenticationResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    roles,
                    user.getLastLoginAt()
                );
                
                return ResponseEntity.ok(new JwtAuthenticationResponse(
                    token,
                    requestRefreshToken,
                    jwtExpirationInSeconds,
                    userInfo
                ));
            })
            .orElseThrow(() -> new BusinessException("Geçersiz refresh token"));
    }
    
    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı"));
            
            // Refresh token'ları sil
            refreshTokenService.deleteByUser(user);
            
            log.info("User logged out: {}", user.getUsername());
        }
        
        return ResponseEntity.ok().build();
    }
}
