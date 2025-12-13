package org.steelboard.marketplace.mapper;

import org.springframework.stereotype.Component;
import org.steelboard.marketplace.dto.order.OrderDto;
import org.steelboard.marketplace.dto.order.OrderItemDto;
import org.steelboard.marketplace.entity.Order;

@Component
public class OrderMapper {

    public OrderDto toOrderDto(Order order) {
        return new OrderDto(order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getOrderItems().stream()
                        .map(item -> new OrderItemDto(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getProduct().getPrice(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getProduct().getMainImageUrl())
                        )
                        .toList(),
                order.getUser().getId(),
                order.getUser().getUsername()
        );
    }
}
