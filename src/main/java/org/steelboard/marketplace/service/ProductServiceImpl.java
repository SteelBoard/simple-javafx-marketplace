package org.steelboard.marketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.repository.ProductRepository;

import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findAllProductsByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return productRepository.findAllProductsByNameIgnoreCase(name, pageable);
    }

    @Override
    public Page<Product> findAllProductsBySeller(User seller, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("seller"));
        return productRepository.findAllBySeller(seller, pageable);
    }
}
