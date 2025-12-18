package org.steelboard.marketplace.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductStatDto {
    private Long productId;
    private String productName;
    private Long totalSoldQuantity;
    private BigDecimal totalRevenue;
}