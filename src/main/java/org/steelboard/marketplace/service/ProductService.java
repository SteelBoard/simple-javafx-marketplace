package org.steelboard.marketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.User;

import java.util.List;

public interface ProductService {

    Page<Product> getAllProducts(int page, int size);
    Page<Product> findAllProductsByName(String name, int page, int size);
    Page<Product> findAllProductsBySeller(User seller, int page, int size);
    List<Product> saveAllProducts(List<Product> products);
    Product saveProduct(Product product);
    boolean deleteProduct(Product product);
    Long countAllProducts();
}
