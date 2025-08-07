package mertcan.usermanagement.command.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.mediator.IRequest;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRolesCommand implements IRequest<UserDto> {
    
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    
    @NotEmpty(message = "At least one role must be provided")
    private Set<String> roleNames;
}
