package org.steelboard.marketplace.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AddProductDto {

    @NotBlank(message = "Название продукта обязательно")
    @Size(max = 100, message = "Название продукта должно быть не длиннее 100 символов")
    private String name;

    @NotBlank(message = "Описание продукта обязательно")
    @Size(max = 1000, message = "Описание продукта должно быть не длиннее 1000 символов")
    private String description;

    @NotNull(message = "Цена продукта обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть положительной")
    private BigDecimal price;

    @NotNull(message = "Главная картинка обязательна")
    private MultipartFile mainImage;

    private List<MultipartFile> additionalImages;
}
