package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.steelboard.marketplace.dto.product.ProductEditDto;
import org.steelboard.marketplace.entity.*;
import org.steelboard.marketplace.exception.ProductNotFoundException;
import org.steelboard.marketplace.repository.ProductImageRepository;
import org.steelboard.marketplace.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FileStorageService fileStorageService;

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProduct(Long id) {
        return productRepository.findProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> findBySellerId(Long id) {
        return productRepository.findBySeller_Id(id);
    }

    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public ProductEditDto toEditDto(Product product) {
        ProductEditDto dto = new ProductEditDto();
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setActive(product.getActive());
        return dto;
    }

    @Transactional
    public void updateProductFromDto(Long id, ProductEditDto dto) {
        Product product = getProduct(id);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setActive(dto.getActive() != null ? dto.getActive() : false); 
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void deleteImage(Long productId, Long imageId) {
        Product product = getProduct(productId);
        
        product.getImages().removeIf(img -> img.getId().equals(imageId));
        productRepository.save(product);
    }


    @Transactional
    public void updateRating(Long productId, List<Review> reviews) {
        Product product = getProduct(productId);

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        product.setRating(Math.round(average * 100.0) / 100.0); 
        productRepository.save(product);
    }

    @Transactional
    public Product createProduct(String name,
                              String description,
                              BigDecimal price,
                              String mainImagePath,
                              List<String> additionalImagePaths,
                              User seller) {

        
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setSeller(seller);

        
        product = productRepository.save(product);

        
        if (mainImagePath != null) {
            ProductImage mainImage = new ProductImage();
            mainImage.setFilepath(mainImagePath);
            mainImage.setProduct(product);
            mainImage.setType(ImageType.MAIN); 
            mainImage.setSortOrder(0);
            productImageRepository.save(mainImage);
        }

        
        if (additionalImagePaths != null && !additionalImagePaths.isEmpty()) {
            int order = 1; 
            for (String path : additionalImagePaths) {
                ProductImage image = new ProductImage();
                image.setFilepath(path);
                image.setProduct(product);
                image.setType(ImageType.GALLERY); 
                image.setSortOrder(order++);
                productImageRepository.save(image);
            }
        }

        return product;
    }

    
    public void addImage(Long productId, MultipartFile file) {
        if (file.isEmpty()) return;

        Product product = getProduct(productId);
        String filePath = fileStorageService.saveFile(file, "products");

        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setFilepath(filePath);

        
        boolean hasMain = product.getImages().stream()
                .anyMatch(img -> img.getType() == ImageType.MAIN);

        if (!hasMain) {
            image.setType(ImageType.MAIN);
            image.setSortOrder(0);
        } else {
            image.setType(ImageType.GALLERY);
            image.setSortOrder(product.getImages().size() + 1);
        }

        productImageRepository.save(image);
    }

    
    @Transactional
    public void setMainImage(Long productId, Long imageId) {
        Product product = getProduct(productId);

        for (ProductImage img : product.getImages()) {
            if (img.getId().equals(imageId)) {
                
                img.setType(ImageType.MAIN);
                img.setSortOrder(0);
            } else {
                
                if (img.getType() == ImageType.MAIN) {
                    img.setType(ImageType.GALLERY);
                    img.setSortOrder(1); 
                }
            }
        }
        productRepository.save(product);
    }


    public void incrementProductSales(Long productId, int quantity) {
        productRepository.incrementSales(productId, quantity);
    }
}
