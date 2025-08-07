package mertcan.usermanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {
    
    private final RefreshTokenService refreshTokenService;
    
    @Scheduled(cron = "0 0 2 * * ?") // Her gün saat 02:00'da çalışır
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired refresh tokens");
        refreshTokenService.deleteExpiredTokens();
        log.info("Completed cleanup of expired refresh tokens");
    }
}
