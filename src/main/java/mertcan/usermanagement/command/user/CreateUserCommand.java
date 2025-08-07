package mertcan.usermanagement.command.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.mediator.IRequest;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserCommand implements IRequest<UserDto> {
    
    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(min = 3, max = 50, message = "Kullanıcı adı 3-50 karakter arasında olmalıdır")
    private String username;
    
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;
    
    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String password;
    
    @NotBlank(message = "Ad boş olamaz")
    @Size(max = 50, message = "Ad en fazla 50 karakter olabilir")
    private String firstName;
    
    @NotBlank(message = "Soyad boş olamaz")
    @Size(max = 50, message = "Soyad en fazla 50 karakter olabilir")
    private String lastName;
    
    @Size(max = 20, message = "Telefon numarası en fazla 20 karakter olabilir")
    private String phoneNumber;
    
    private Set<String> roleNames;
}
