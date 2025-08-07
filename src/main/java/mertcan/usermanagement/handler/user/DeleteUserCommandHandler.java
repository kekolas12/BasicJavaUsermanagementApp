package mertcan.usermanagement.handler.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.command.user.DeleteUserCommand;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.exception.UserNotFoundException;
import mertcan.usermanagement.mediator.IRequestHandler;
import mertcan.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteUserCommandHandler implements IRequestHandler<DeleteUserCommand, Void> {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public Void handle(DeleteUserCommand command) {
        log.info("Deleting user with ID: {}", command.getId());
        
        User user = userRepository.findById(command.getId())
            .orElseThrow(() -> new UserNotFoundException(command.getId()));
        
        // Kullanıcıyı sil
        userRepository.delete(user);
        
        log.info("User deleted successfully with ID: {}", command.getId());
        
        return null;
    }
}
