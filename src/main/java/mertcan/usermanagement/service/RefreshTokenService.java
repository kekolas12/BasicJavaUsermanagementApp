package mertcan.usermanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.entity.RefreshToken;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.exception.BusinessException;
import mertcan.usermanagement.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    
    @Value("${jwt.expiration}")
    private long jwtExpirationInSeconds;
    
    private final RefreshTokenRepository refreshTokenRepository;
    
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Kullanıcının mevcut refresh token'larını sil
        refreshTokenRepository.deleteByUser(user);
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(jwtExpirationInSeconds * 7)); // 7 gün
        
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created for user: {}", user.getUsername());
        
        return savedToken;
    }
    
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new BusinessException("Refresh token süresi dolmuş. Lütfen yeniden giriş yapın.");
        }
        
        return token;
    }
    
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
        log.info("Refresh tokens deleted for user: {}", user.getUsername());
    }
    
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Expired refresh tokens cleaned up");
    }
}
