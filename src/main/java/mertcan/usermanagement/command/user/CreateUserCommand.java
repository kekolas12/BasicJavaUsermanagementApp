package mertcan.usermanagement.command.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.mediator.IRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserCommand implements IRequest<UserDto> {
    
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3-50 characters")
    private String username;
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please enter a valid email address")
    private String email;
    
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "First name cannot be empty")
    @Size(max = 50, message = "First name can be at most 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name cannot be empty")
    @Size(max = 50, message = "Last name can be at most 50 characters")
    private String lastName;
    
    @Size(max = 20, message = "Phone number can be at most 20 characters")
    private String phoneNumber;
}
