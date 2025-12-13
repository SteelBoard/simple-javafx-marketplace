package org.steelboard.marketplace.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {

    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters")
    private String phoneNumber;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String confirmPassword;

    // Проверка на обновление пароля
    public boolean isPasswordBeingUpdated() {
        return password != null && !password.isBlank();
    }
}
