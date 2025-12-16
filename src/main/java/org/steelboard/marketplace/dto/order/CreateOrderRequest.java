package org.steelboard.marketplace.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {

    @NotEmpty(message = "Выберите хотя бы один товар")
    private List<Long> selectedProductIds;

    @NotNull(message = "Выберите пункт выдачи")
    private Long pickupPointId;

    @NotNull
    private BigDecimal totalPrice;
}
