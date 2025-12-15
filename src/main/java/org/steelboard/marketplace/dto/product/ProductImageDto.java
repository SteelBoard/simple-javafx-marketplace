package org.steelboard.marketplace.dto.product;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import org.steelboard.marketplace.entity.ImageType;

@Data
public class ProductImageDto {
    MultipartFile file;
    ImageType imageType;
}
