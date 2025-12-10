package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.repository.ProductRepository;

import java.util.Optional;

@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Optional<Product> getProduct(Long id) {
        return productRepository.findProductById(id);
    }
}
