package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.steelboard.marketplace.dto.product.ProductDto;
import org.steelboard.marketplace.repository.ProductRepository;

@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductDto> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductDto::new);
    }
}
