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

    @NotBlank(message = "Телефон обязателен")
    // Используем тот же строгий паттерн, что и при регистрации
    @Pattern(regexp = "^\\+7 \\d{3} \\d{3}-\\d{2}-\\d{2}$", message = "Формат: +7 999 000-00-00")
    private String phoneNumber;

    // Убрали @NotBlank, так как смена пароля опциональна.
    // Но если пароль введен, он должен быть не менее 6 символов.
    // Примечание: Стандартный @Size может сработать на пустую строку,
    // поэтому логику длины пароля при обновлении часто выносят в кастомный валидатор или проверяют в контроллере.
    // Однако, для телефона главное изменение выше.
    private String password;

    private String confirmPassword;

    public boolean isPasswordBeingUpdated() {
        return password != null && !password.isBlank();
    }
}