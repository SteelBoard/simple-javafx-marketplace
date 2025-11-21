package org.steelboard.marketplace.util;

import lombok.AllArgsConstructor;
import org.steelboard.marketplace.entity.Product;

public record ProductCreatedEvent(Product product) {
}

