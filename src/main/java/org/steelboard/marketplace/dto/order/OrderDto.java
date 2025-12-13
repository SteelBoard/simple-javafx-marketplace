package org.steelboard.marketplace.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.steelboard.marketplace.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private OrderStatus orderStatus;
    private BigDecimal price;
    private List<OrderItemDto> orderItems;
    private Long userId;
    private String userName;
}
