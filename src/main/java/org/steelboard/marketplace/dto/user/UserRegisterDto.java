package org.steelboard.marketplace.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDto {

    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 20, message = "От 3 до 20 символов")
    private String username;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат Email")
    private String email;

    @NotBlank(message = "Телефон обязателен")

    @Pattern(regexp = "^\\+7 \\d{3} \\d{3}-\\d{2}-\\d{2}$", message = "Формат: +7 999 000-00-00")
    private String phoneNumber;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Минимум 6 символов")
    private String password;

    @NotBlank(message = "Подтвердите пароль")
    private String confirmPassword;


}
