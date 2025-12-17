package org.steelboard.marketplace.dto.product;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.steelboard.marketplace.entity.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class ProductEditDto {

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    private BigDecimal price;

    private Boolean active = false;

    private List<Long> imageIdsToDelete =  new ArrayList<>();

    public ProductEditDto(Product product) {
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.active = product.getActive();
        this.imageIdsToDelete = new ArrayList<>();
    }
}
