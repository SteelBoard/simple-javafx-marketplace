package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.ImageType;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.ProductImage;
import org.steelboard.marketplace.exception.ProductNotFoundException;
import org.steelboard.marketplace.repository.ProductImageRepository;
import org.steelboard.marketplace.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProduct(Long id) {
        return productRepository.findProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public void createProduct(String name,
                              String description,
                              BigDecimal price,
                              String mainImagePath,
                              List<String> additionalImagePaths) {

        // 1. Создаём продукт
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);

        // сохраняем продукт, чтобы получить id для FK
        product = productRepository.save(product);

        // 2. Сохраняем главную картинку
        if (mainImagePath != null) {
            ProductImage mainImage = new ProductImage();
            mainImage.setFilepath(mainImagePath);
            mainImage.setProduct(product);
            mainImage.setType(ImageType.MAIN); // предполагаем, что есть MAIN
            mainImage.setSortOrder(0);
            productImageRepository.save(mainImage);
        }

        // 3. Сохраняем дополнительные картинки
        if (additionalImagePaths != null && !additionalImagePaths.isEmpty()) {
            int order = 1; // сортировка начиная с 1
            for (String path : additionalImagePaths) {
                ProductImage image = new ProductImage();
                image.setFilepath(path);
                image.setProduct(product);
                image.setType(ImageType.GALLERY); // дополнительные
                image.setSortOrder(order++);
                productImageRepository.save(image);
            }
        }
    }
}
