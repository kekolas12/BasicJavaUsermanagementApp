package mertcan.usermanagement.command.user;

import jakarta.validation.constraints.NotEmpty;
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
    
    @NotEmpty(message = "At least one role must be provided")
    private Set<String> roleNames;
    
    // This field will be set by the controller from path parameter
    private Long userId;
    
    public UpdateUserRolesCommand(Set<String> roleNames) {
        this.roleNames = roleNames;
    }
}
