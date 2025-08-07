package mertcan.usermanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mertcan.usermanagement.command.user.CreateUserCommand;
import mertcan.usermanagement.command.user.DeleteUserCommand;
import mertcan.usermanagement.command.user.UpdateUserCommand;
import mertcan.usermanagement.command.user.UpdateUserRolesCommand;
import mertcan.usermanagement.dto.UserDto;
import mertcan.usermanagement.exception.ErrorResponse;
import mertcan.usermanagement.mediator.IMediator;
import mertcan.usermanagement.query.user.GetAllUsersQuery;
import mertcan.usermanagement.query.user.GetUserByIdQuery;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Kullanıcı yönetimi API'leri - CRUD işlemleri ve kullanıcı sorguları")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    private final IMediator mediator;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Yeni kullanıcı oluştur", 
        description = "Yeni bir kullanıcı oluşturur. Sadece ADMIN rolüne sahip kullanıcılar bu işlemi gerçekleştirebilir.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Oluşturulacak kullanıcının bilgileri",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreateUserCommand.class),
                examples = @ExampleObject(
                    name = "Örnek Kullanıcı",
                    value = """
                        {
                            "username": "johndoe",
                            "email": "john.doe@example.com",
                            "password": "SecurePassword123!",
                            "firstName": "John",
                            "lastName": "Doe"
                        }
                        """
                )
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Kullanıcı başarıyla oluşturuldu",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class),
                examples = @ExampleObject(
                    name = "Oluşturulmuş Kullanıcı",
                    value = """
                        {
                            "id": 1,
                            "username": "johndoe",
                            "email": "john.doe@example.com",
                            "firstName": "John",
                            "lastName": "Doe",
                            "isActive": true,
                            "createdAt": "2025-08-06T19:30:00",
                            "updatedAt": "2025-08-06T19:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Geçersiz girdi - Validation hatası",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Validation Hatası",
                    value = """
                        {
                            "status": 400,
                            "message": "Doğrulama hatası",
                            "timestamp": "2025-08-06T19:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Kullanıcı adı veya email zaten mevcut",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Bu işlem için yetkiniz bulunmamaktadır",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody 
            @Parameter(description = "Oluşturulacak kullanıcının bilgileri", required = true)
            CreateUserCommand command) {
        UserDto userDto = mediator.send(command);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or #id == authentication.principal.id")
    @Operation(
        summary = "Kullanıcı getir", 
        description = "ID'ye göre kullanıcı getirir. ADMIN ve MODERATOR rolleri tüm kullanıcıları görebilir, diğer kullanıcılar sadece kendi bilgilerini görebilir."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Kullanıcı başarıyla bulundu",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class),
                examples = @ExampleObject(
                    name = "Kullanıcı Bilgileri",
                    value = """
                        {
                            "id": 1,
                            "username": "johndoe",
                            "email": "john.doe@example.com",
                            "firstName": "John",
                            "lastName": "Doe",
                            "isActive": true,
                            "roles": ["USER"],
                            "createdAt": "2025-08-06T19:30:00",
                            "updatedAt": "2025-08-06T19:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Kullanıcı bulunamadı",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Kullanıcı Bulunamadı",
                    value = """
                        {
                            "status": 404,
                            "message": "Kullanıcı bulunamadı",
                            "timestamp": "2025-08-06T19:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Bu kullanıcıyı görüntüleme yetkiniz bulunmamaktadır",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<UserDto> getUserById(
            @Parameter(
                description = "Getirilecek kullanıcının ID'si", 
                required = true,
                example = "1",
                schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id) {
        UserDto userDto = mediator.send(new GetUserByIdQuery(id));
        return ResponseEntity.ok(userDto);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(
        summary = "Tüm kullanıcıları listele", 
        description = "Sayfalama, sıralama ve filtreleme özellikleri ile kullanıcıları listeler. Sadece ADMIN ve MODERATOR rolleri kullanabilir."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Kullanıcılar başarıyla listelendi",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Kullanıcı Listesi",
                    value = """
                        {
                            "content": [
                                {
                                    "id": 1,
                                    "username": "johndoe",
                                    "email": "john.doe@example.com",
                                    "firstName": "John",
                                    "lastName": "Doe",
                                    "isActive": true,
                                    "roles": ["USER"],
                                    "createdAt": "2025-08-06T19:30:00",
                                    "updatedAt": "2025-08-06T19:30:00"
                                }
                            ],
                            "pageable": {
                                "sort": {
                                    "sorted": true,
                                    "ascending": true
                                },
                                "pageNumber": 0,
                                "pageSize": 10
                            },
                            "totalElements": 1,
                            "totalPages": 1,
                            "first": true,
                            "last": true,
                            "numberOfElements": 1
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Bu işlem için yetkiniz bulunmamaktadır",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @Parameter(
                description = "Sayfa numarası (0'dan başlar)", 
                example = "0",
                schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")
            ) 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(
                description = "Sayfa boyutu (her sayfada kaç kullanıcı)", 
                example = "10",
                schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "10")
            ) 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(
                description = "Sıralama alanı (id, username, email, firstName, lastName, createdAt)", 
                example = "id",
                schema = @Schema(type = "string", defaultValue = "id", allowableValues = {"id", "username", "email", "firstName", "lastName", "createdAt"})
            ) 
            @RequestParam(defaultValue = "id") String sortBy,
            
            @Parameter(
                description = "Sıralama yönü", 
                example = "asc",
                schema = @Schema(type = "string", defaultValue = "asc", allowableValues = {"asc", "desc"})
            ) 
            @RequestParam(defaultValue = "asc") String sortDir,
            
            @Parameter(
                description = "Arama terimi (username, email, firstName, lastName alanlarında arar)", 
                example = "john"
            ) 
            @RequestParam(required = false) String search,
            
            @Parameter(
                description = "Aktif durum filtresi (true: sadece aktif, false: sadece pasif, null: hepsi)", 
                example = "true"
            ) 
            @RequestParam(required = false) Boolean isActive) {

        GetAllUsersQuery query = new GetAllUsersQuery(page, size, sortBy, sortDir, search, isActive);
        Page<UserDto> users = mediator.send(query);
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(
        summary = "Kullanıcı güncelle", 
        description = "Mevcut kullanıcının bilgilerini günceller. ADMIN rolü tüm kullanıcıları güncelleyebilir, diğer kullanıcılar sadece kendi bilgilerini güncelleyebilir.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Güncellenecek kullanıcı bilgileri",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateUserCommand.class),
                examples = @ExampleObject(
                    name = "Kullanıcı Güncelleme",
                    value = """
                        {
                            "email": "john.doe.updated@example.com",
                            "firstName": "John Updated",
                            "lastName": "Doe Updated",
                            "password": "NewSecurePassword123!",
                            "isActive": true,
                            "roleNames": ["USER", "MODERATOR"]
                        }
                        """
                )
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Kullanıcı başarıyla güncellendi",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class),
                examples = @ExampleObject(
                    name = "Güncellenmiş Kullanıcı",
                    value = """
                        {
                            "id": 1,
                            "username": "johndoe",
                            "email": "john.doe.updated@example.com",
                            "firstName": "John Updated",
                            "lastName": "Doe Updated",
                            "isActive": true,
                            "roles": ["USER", "MODERATOR"],
                            "createdAt": "2025-08-06T19:30:00",
                            "updatedAt": "2025-08-06T19:35:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Kullanıcı bulunamadı",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Geçersiz girdi - Validation hatası",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Bu kullanıcıyı güncelleme yetkiniz bulunmamaktadır",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Email zaten başka bir kullanıcı tarafından kullanılıyor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<UserDto> updateUser(
            @Parameter(
                description = "Güncellenecek kullanıcının ID'si", 
                required = true,
                example = "1",
                schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id,
            
            @Valid @RequestBody 
            @Parameter(description = "Güncellenecek kullanıcı bilgileri", required = true)
            UpdateUserCommand command) {
        command.setId(id);
        UserDto userDto = mediator.send(command);
        return ResponseEntity.ok(userDto);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Kullanıcı sil", 
        description = "Kullanıcıyı sistemden kalıcı olarak siler. Bu işlem geri alınamaz ve sadece ADMIN rolüne sahip kullanıcılar gerçekleştirebilir."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Kullanıcı başarıyla silindi - İçerik döndürülmez"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Kullanıcı bulunamadı",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Kullanıcı Bulunamadı",
                    value = """
                        {
                            "status": 404,
                            "message": "Kullanıcı bulunamadı",
                            "timestamp": "2025-08-06T19:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Bu işlem için ADMIN yetkisi gereklidir",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Yetki Hatası",
                    value = """
                        {
                            "status": 403,
                            "message": "Bu işlem için yetkiniz bulunmamaktadır",
                            "timestamp": "2025-08-06T19:30:00"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(
                description = "ID of the user to delete", 
                required = true,
                example = "1",
                schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id) {
        mediator.send(new DeleteUserCommand(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update user roles", 
        description = "Updates roles for an existing user. Only users with ADMIN role can perform this operation.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "New roles to assign to the user",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateUserRolesCommand.class),
                examples = @ExampleObject(
                    name = "Example Role Update",
                    value = """
                    {
                        "roleNames": ["USER", "MODERATOR"]
                    }
                    """
                )
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User roles updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Access denied - Admin role required",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<UserDto> updateUserRoles(
            @Parameter(
                description = "ID of the user whose roles will be updated", 
                required = true,
                example = "1",
                schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRolesCommand command) {
        
        // Set the user ID from path parameter
        command.setUserId(id);
        
        UserDto updatedUser = mediator.send(command);
        return ResponseEntity.ok(updatedUser);
    }
}
