package mertcan.usermanagement.command.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mertcan.usermanagement.mediator.IRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserCommand implements IRequest<Void> {
    private Long id;
}
