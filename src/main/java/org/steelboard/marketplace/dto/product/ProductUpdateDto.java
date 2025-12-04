package org.steelboard.marketplace.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto {

    private String name;
    private String description;

    @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
    private Double price;

    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Long quantity;

    private Boolean active;


}
