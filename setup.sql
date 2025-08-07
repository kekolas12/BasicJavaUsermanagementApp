-- User Management Database Setup Script
-- Bu dosyayı MySQL'de çalıştırarak veritabanını oluşturun

-- 1. Veritabanını oluştur (eğer yoksa)
CREATE DATABASE IF NOT EXISTS usermanagement 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 2. Veritabanını kullan
USE usermanagement;

-- 3. Root kullanıcısına tam yetki ver (geliştirme ortamı için)
GRANT ALL PRIVILEGES ON usermanagement.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

-- 4. Test kullanıcısı oluştur (opsiyonel)
-- CREATE USER IF NOT EXISTS 'usermanagement_user'@'localhost' IDENTIFIED BY 'password123';
-- GRANT ALL PRIVILEGES ON usermanagement.* TO 'usermanagement_user'@'localhost';
-- FLUSH PRIVILEGES;

-- Veritabanı oluşturuldu!
-- Artık Spring Boot uygulamasını çalıştırabilirsiniz.
-- Flyway migration'ları otomatik olarak çalışacak ve tabloları oluşturacak.

SELECT 'Database usermanagement created successfully!' as message;
