package org.steelboard.marketplace.dto.order;

import java.math.BigDecimal;
import java.util.List;

import org.steelboard.marketplace.dto.product.ProductStatDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerStatsDto {
    private BigDecimal totalRevenue;
    private Long ordersCount;
    private List<ProductStatDto> topProducts;
}
