package org.steelboard.marketplace.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.steelboard.marketplace.entity.ImageType;
import org.steelboard.marketplace.entity.Product;

@Data
@NoArgsConstructor
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String sellerName;  // Имя продавца вместо полной сущности User
    private Double rating;
    private Long sales;
    private Long quantity;
    private Boolean active;
    private String imageUrl;    // Первое изображение для отображения

    public ProductDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.sellerName = product.getSeller().getUsername();
        this.rating = product.getRating();
        this.sales = product.getSales();
        this.quantity = product.getQuantity();
        this.active = product.getActive();
        this.imageUrl = product.getImages().stream()
                .filter(image -> image.getType().equals(ImageType.MAIN))
                .map(image -> image.getFilepath() + image.getFileName()).findFirst().orElse(null);
    }
}
