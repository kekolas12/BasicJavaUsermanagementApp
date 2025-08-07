-- V2__Insert_initial_data.sql
-- Insert initial roles and admin user

-- Insert default roles
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'System Administrator with full access'),
('USER', 'Regular user with basic access'),
('MODERATOR', 'User with moderation privileges');

-- Insert admin user (password: admin123)
-- Password hash for "admin123" using BCrypt
INSERT INTO users (username, email, password, first_name, last_name, is_active, is_email_verified) VALUES 
('admin', 'admin@usermanagement.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'User', TRUE, TRUE);

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN';
