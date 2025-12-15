package org.steelboard.marketplace.entity;

import com.sun.javafx.iio.ImageStorage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String filepath;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType type = ImageType.GALLERY;
    private Integer sortOrder = 0;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
}
