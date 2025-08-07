package mertcan.usermanagement.exception;

public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(Long id) {
        super("Kullanıcı bulunamadı: " + id);
    }
}
