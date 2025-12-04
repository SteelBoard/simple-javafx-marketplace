package org.steelboard.marketplace.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageDto {
    private List<ProductDto> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int size;
    private boolean hasPrevious;
    private boolean hasNext;
    private String sortField;
    private String sortOrder;

    public static ProductPageDto fromPage(Page<ProductDto> page, int currentPage, String sortField, String sortOrder) {
        return new ProductPageDto(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                currentPage,
                (int) page.getSize(),
                page.hasPrevious(),
                page.hasNext(),
                sortField,
                sortOrder
        );
    }
}
