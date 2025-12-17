package org.steelboard.marketplace.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.service.ProductService;

@Controller
@AllArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id") String sortField, // По умолчанию сортируем по новизне (ID или date)
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String search, // Добавили параметр поиска
            Model model) {

        Sort sort = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage;

        // Если есть поиск - ищем, если нет - показываем всё
        if (search != null && !search.isEmpty()) {
            productPage = productService.searchProductsByName(search, pageable);
        } else {
            productPage = productService.getProducts(pageable);
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("search", search); // Возвращаем строку поиска, чтобы не исчезала из поля

        return "index";
    }
}