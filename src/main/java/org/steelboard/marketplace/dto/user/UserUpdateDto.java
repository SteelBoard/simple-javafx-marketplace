package org.steelboard.marketplace.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {

    @NotBlank(message = "Username не может быть пустым")
    @Size(min = 3, max = 20, message = "Username от 3 до 20 символов")
    private String username;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат Email")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Телефон должен содержать 10-15 цифр")
    private String phoneNumber;

    private String password;
    private String confirmPassword;

    public boolean isPasswordBeingUpdated() {
        return password != null && !password.isBlank();
    }
}