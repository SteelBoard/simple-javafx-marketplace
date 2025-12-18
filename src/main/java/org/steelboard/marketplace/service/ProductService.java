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
import java.util.UUID;

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

    public Page<Product> findBySellerId(Long sellerId, String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return productRepository.findBySeller_IdAndNameContainingIgnoreCase(sellerId, search.trim(), pageable);
        }
        return productRepository.findBySeller_Id(sellerId, pageable);
    }

    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product with SKU " + sku + " not found"));
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
        // Добавляем SKU в DTO, чтобы видеть его при редактировании
        // Убедитесь, что в классе ProductEditDto есть поле private String sku;
        dto.setSku(product.getSku());
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

        // ВАЖНО: Так как в БД стоит nullable=false, мы должны задать временное значение,
        // чтобы Hibernate дал выполнить первый insert.
        // Используем UUID, чтобы гарантировать уникальность до момента генерации красивого SKU.
        product.setSku(UUID.randomUUID().toString());

        // 1. Первое сохранение для получения ID
        product = productRepository.save(product);

        // 2. Генерация красивого артикула на основе ID
        String sku = "PRD-" + String.format("%06d", product.getId());
        product.setSku(sku);

        // 3. Второе сохранение (обновление) с новым SKU
        product = productRepository.save(product);

        // Сохранение изображений
        if (mainImagePath != null) {
            saveImage(product, mainImagePath, ImageType.MAIN, 0);
        }

        if (additionalImagePaths != null && !additionalImagePaths.isEmpty()) {
            int order = 1;
            for (String path : additionalImagePaths) {
                saveImage(product, path, ImageType.GALLERY, order++);
            }
        }

        return product;
    }

    private void saveImage(Product product, String path, ImageType type, int sortOrder) {
        ProductImage image = new ProductImage();
        image.setFilepath(path);
        image.setProduct(product);
        image.setType(type);
        image.setSortOrder(sortOrder);
        productImageRepository.save(image);
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
