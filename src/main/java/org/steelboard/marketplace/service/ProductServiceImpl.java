package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

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

    @Override
    public List<Product> saveAllProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public boolean deleteProduct(Product product) {
        return productRepository.deleteProductById(product.getId());
    }

    @Override
    public Long countAllProducts() {
        return  productRepository.count();
    }
}
