package org.steelboard.marketplace.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.steelboard.marketplace.dto.product.ProductEditDto;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.ProductService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my/products")
public class SellerProductController {

    private final ProductService productService;

    // 1. СПИСОК ТОВАРОВ
    @GetMapping
    public String myProducts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        // Метод ищет товары по ID продавца (и активные, и скрытые)
        Page<Product> productsPage = productService.getSellerProducts(user.getId(), null, pageable);

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("user", user);
        return "seller_products"; // Имя вашего HTML файла списка
    }

    // 2. СТРАНИЦА РЕДАКТИРОВАНИЯ (GET)
    @GetMapping("/{sku}/edit")
    public String editProductPage(@PathVariable String sku,
                                  @AuthenticationPrincipal User user,
                                  Model model) {
        Product product = productService.getProductBySku(sku);

        if (!product.getSeller().getId().equals(user.getId())) {
            return "redirect:/my/products?error=access_denied";
        }

        if (!model.containsAttribute("productDto")) {
            model.addAttribute("productDto", productService.toEditDto(product));
        }

        model.addAttribute("product", product);

        // ВАЖНО: Добавляем пользователя для Sidebar!
        model.addAttribute("user", user);

        return "seller_edit"; // Имя файла шаблона
    }

    // 3. ОБНОВЛЕНИЕ ТОВАРА (POST)
    @PostMapping("/{sku}/update")
    public String updateProduct(
            @PathVariable String sku,
            @AuthenticationPrincipal User user,
            @Valid @ModelAttribute("productDto") ProductEditDto dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        Product product = productService.getProductBySku(sku);

        if (!product.getSeller().getId().equals(user.getId())) {
            return "redirect:/my/products?error";
        }

        if (result.hasErrors()) {
            model.addAttribute("product", product);
            model.addAttribute("user", user); // ВАЖНО: Добавляем пользователя при возврате ошибок
            return "seller_edit";
        }

        productService.updateProductFromDto(product.getId(), dto);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/my/products/" + sku + "/edit";
    }

    // 4. МЯГКОЕ УДАЛЕНИЕ (POST)
    @PostMapping("/{sku}/delete")
    public String deleteProduct(@PathVariable String sku,
                                @AuthenticationPrincipal User user,
                                RedirectAttributes redirectAttributes) {
        Product product = productService.getProductBySku(sku);

        // ЗАЩИТА
        if (!product.getSeller().getId().equals(user.getId())) {
            return "redirect:/my/products?error";
        }

        // Вместо удаления делаем деактивацию
        productService.softDeleteProduct(product.getId(), user.getId());

        redirectAttributes.addFlashAttribute("success", "Товар перемещен в архив.");
        return "redirect:/my/products";
    }

    // 5. РАБОТА С ФОТО (Добавление)
    @PostMapping("/{sku}/images/add")
    public String addImage(@PathVariable String sku,
                           @RequestParam("file") MultipartFile file,
                           @AuthenticationPrincipal User user) {
        Product product = productService.getProductBySku(sku);

        if (product.getSeller().getId().equals(user.getId())) {
            productService.addImage(product.getId(), file);
        }
        return "redirect:/my/products/" + sku + "/edit";
    }

    // 6. РАБОТА С ФОТО (Удаление)
    @PostMapping("/{sku}/images/delete")
    public String deleteImage(@PathVariable String sku,
                              @RequestParam Long imageId,
                              @AuthenticationPrincipal User user) {
        Product product = productService.getProductBySku(sku);

        if (product.getSeller().getId().equals(user.getId())) {
            productService.deleteImage(product.getId(), imageId);
        }
        return "redirect:/my/products/" + sku + "/edit";
    }

    // 7. РАБОТА С ФОТО (Главная)
    @PostMapping("/{sku}/images/set-main")
    public String setMainImage(@PathVariable String sku,
                               @RequestParam Long imageId,
                               @AuthenticationPrincipal User user) {
        Product product = productService.getProductBySku(sku);

        if (product.getSeller().getId().equals(user.getId())) {
            productService.setMainImage(product.getId(), imageId);
        }
        return "redirect:/my/products/" + sku + "/edit";
    }
}