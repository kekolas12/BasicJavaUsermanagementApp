package mertcan.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class UsermanagementApplication {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    public static void main(String[] args) {
        SpringApplication.run(UsermanagementApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        log.info("\n" +
            "=======================================================\n" +
            "🚀 USER MANAGEMENT API BAŞARILI ŞEKİLDE BAŞLATILDI! 🚀\n" +
            "=======================================================\n" +
            "📍 Ana URL: http://localhost:{}{}\n" +
            "📖 Swagger UI: http://localhost:{}{}/swagger-ui.html\n" +
            "📋 API Docs: http://localhost:{}{}/v3/api-docs\n" +
            "💖 Health Check: http://localhost:{}{}/actuator/health\n" +
            "=======================================================\n" +
            "🔐 TEST KULLANICI BİLGİLERİ:\n" +
            "   👤 Username: admin\n" +
            "   🔑 Password: admin123\n",
            serverPort, contextPath,
            serverPort, contextPath,
            serverPort, contextPath,
            serverPort, contextPath
        );
    }
}
