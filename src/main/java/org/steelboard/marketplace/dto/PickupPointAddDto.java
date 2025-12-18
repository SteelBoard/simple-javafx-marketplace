package org.steelboard.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PickupPointAddDto {

    @NotBlank(message = "Город обязателен")
    private String city;

    @NotBlank(message = "Улица обязательна")
    private String street;

    @NotBlank(message = "Номер дома обязателен")
    private String houseNumber;

    private String apartmentNumber; // Может быть пустым

    @NotBlank(message = "Индекс обязателен")
    @Size(min = 6, max = 6, message = "Индекс должен состоять из 6 цифр")
    @Pattern(regexp = "\\d{6}", message = "Индекс должен содержать только цифры")
    private String postalCode;

    @NotBlank(message = "Телефон обязателен")
    // Строгая проверка формата: +7 999 111-22-33
    @Pattern(regexp = "^\\+7 \\d{3} \\d{3}-\\d{2}-\\d{2}$", message = "Формат телефона: +7 999 111-22-33")
    private String phone;
}