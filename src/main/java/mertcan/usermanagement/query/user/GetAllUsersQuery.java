package mertcan.usermanagement.query.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.mediator.IRequest;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllUsersQuery implements IRequest<Page<UserDto>> {
    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String sortDir = "asc";
    private String search;
    private Boolean isActive;
}
