package org.steelboard.marketplace.dto.cart;

import lombok.Data;

@Data
public class UpdateCartItemDto {
    private Long productId;
    private int quantity;
}
