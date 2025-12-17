package org.steelboard.marketplace.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.steelboard.marketplace.dto.user.UserUpdateDto;
import org.steelboard.marketplace.entity.*;
import org.steelboard.marketplace.service.OrderService;
import org.steelboard.marketplace.service.ProductService;
import org.steelboard.marketplace.service.ReviewService;
import org.steelboard.marketplace.service.UserService;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping
public class ProfileController {

    private final UserService userService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final ProductService productService;

    @GetMapping("/profile")
    public String getProfile(@AuthenticationPrincipal User userDetails, Model model) {
        // Загружаем свежего юзера из БД
        User user = userService.findById(userDetails.getId());

        // Передаем юзера для отображения в сайдбаре (имя, роль)
        model.addAttribute("user", user);

        // Заполняем форму
        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());

        model.addAttribute("userUpdateDto", dto);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal User userDetails,
            @Valid @ModelAttribute("userUpdateDto") UserUpdateDto dto,
            BindingResult bindingResult,
            Model model
    ) {
        User user = userService.findById(userDetails.getId());

        // 1. Проверка паролей
        if (dto.isPasswordBeingUpdated() && !dto.getPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.userUpdateDto", "Пароли не совпадают");
        }

        // 2. Проверка уникальности username (только если он изменился)
        if (!user.getUsername().equals(dto.getUsername()) && userService.usernameExists(dto.getUsername())) {
            bindingResult.rejectValue("username", "error.userUpdateDto", "Этот Username уже занят");
        }

        // Если есть ошибки - возвращаем форму НАЗАД
        if (bindingResult.hasErrors()) {
            // ВАЖНО: Возвращаем объект user, чтобы сайдбар не сломался
            model.addAttribute("user", user);
            return "profile";
        }

        userService.updateUser(user, dto);

        // Обновляем данные юзера для модели (чтобы сразу отобразилось новое имя в сайдбаре)
        user = userService.findById(userDetails.getId()); // или просто обновить поля в объекте
        model.addAttribute("user", user);

        model.addAttribute("successMessage", "Профиль успешно обновлен");
        return "profile";
    }

    @GetMapping("/orders/my") // Маппинг изменил на более логичный /profile/orders, но оставим как ты просил
    // Лучше сделай @GetMapping("/my-orders") или внутри ProfileController
    public String getMyOrders(@AuthenticationPrincipal User userDetails, Model model) {
        User user = userService.findById(userDetails.getId());
        model.addAttribute("user", user); // Для сайдбара

        // Достаем заказы
        List<Order> orders = orderService.findByUserId(user.getId());
        model.addAttribute("orders", orders);

        return "my_orders";
    }

    // --- МОИ ОТЗЫВЫ ---
    @GetMapping("/reviews/my")
    public String getMyReviews(@AuthenticationPrincipal User userDetails, Model model) {
        User user = userService.findById(userDetails.getId());
        model.addAttribute("user", user);

        List<Review> reviews = reviewService.findByUserId(user.getId());
        model.addAttribute("reviews", reviews);

        return "my_reviews";
    }

    // --- МОИ ТОВАРЫ (Продавец) ---
    @GetMapping("/seller/products")
    public String getMyProducts(@AuthenticationPrincipal User userDetails, Model model) {
        User user = userService.findById(userDetails.getId());
        model.addAttribute("user", user);

        List<Product> products = productService.findBySellerId(user.getId());
        model.addAttribute("products", products);

        return "seller_products";
    }
}