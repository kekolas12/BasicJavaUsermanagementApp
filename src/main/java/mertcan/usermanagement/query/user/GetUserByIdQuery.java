package mertcan.usermanagement.query.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.mediator.IRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserByIdQuery implements IRequest<UserDto> {
    private Long id;
}
