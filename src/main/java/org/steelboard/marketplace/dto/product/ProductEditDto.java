package org.steelboard.marketplace.dto.product; // или controller.dto

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductEditDto {

    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 3, max = 255, message = "Название должно быть от 3 до 255 символов")
    private String name;

    @Size(max = 2000, message = "Описание слишком длинное (макс 2000 символов)")
    private String description;

    @NotNull(message = "Укажите цену")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal price;

    private Boolean active;

    private String sku;
}