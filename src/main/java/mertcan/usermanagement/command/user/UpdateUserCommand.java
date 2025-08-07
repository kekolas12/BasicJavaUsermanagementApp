package mertcan.usermanagement.command.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.mediator.IRequest;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserCommand implements IRequest<UserDto> {
    
    private Long id;
    
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;
    
    @Size(max = 50, message = "Ad en fazla 50 karakter olabilir")
    private String firstName;
    
    @Size(max = 50, message = "Soyad en fazla 50 karakter olabilir")
    private String lastName;
    
    @Size(max = 20, message = "Telefon numarası en fazla 20 karakter olabilir")
    private String phoneNumber;
    
    private Boolean isActive;
    
    private Set<String> roleNames;
}
