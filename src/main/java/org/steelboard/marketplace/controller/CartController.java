package org.steelboard.marketplace.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.CartService;
import org.steelboard.marketplace.service.ProductService;
import org.steelboard.marketplace.service.UserService;

@AllArgsConstructor
@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    public String cartPage(Model model,
                           @AuthenticationPrincipal User userDetails) {
        Cart cart = userService.findById(userDetails.getId()).getCart();
        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalPrice", cart.getTotalPrice());
        return "cart";
    }

    @GetMapping("/add/{sku}")
    public String addToCart(
            @PathVariable String sku,
            @AuthenticationPrincipal User userDetails,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            // Получаем ID товара по SKU
            Product product = productService.getProductBySku(sku);

            // Пытаемся добавить (тут сработает проверка из Сервиса)
            cartService.addProductToCart(product.getId(), userService.findById(userDetails.getId()));

            redirectAttributes.addFlashAttribute("success", "Товар добавлен в корзину");
            return "redirect:/cart"; // Или остаться на странице товара

        } catch (IllegalStateException e) {
            // ЛОВИМ ОШИБКУ "Свой товар"
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/product/" + sku; // Возвращаем на страницу товара с ошибкой
        }
    }
}
