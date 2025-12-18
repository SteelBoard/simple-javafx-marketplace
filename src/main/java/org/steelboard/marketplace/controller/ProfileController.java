package org.steelboard.marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    @GetMapping("/profile")
    public String getProfile(@AuthenticationPrincipal User userDetails, Model model) {
        User user = userService.findById(userDetails.getId());
        model.addAttribute("user", user);

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

        if (dto.isPasswordBeingUpdated() && !dto.getPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.userUpdateDto", "Пароли не совпадают");
        }

        if (!user.getUsername().equals(dto.getUsername()) && userService.usernameExists(dto.getUsername())) {
            bindingResult.rejectValue("username", "error.userUpdateDto", "Этот Username уже занят");
        }

        if (dto.isPasswordBeingUpdated() && dto.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "error.userUpdateDto", "Пароль должен быть минимум 6 символов");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "profile";
        }

        userService.updateUser(user, dto);

        user = userService.findById(userDetails.getId());
        model.addAttribute("user", user);
        model.addAttribute("successMessage", "Профиль успешно обновлен");
        return "profile";
    }

    @GetMapping("/orders/my")
    public String getMyOrders(@AuthenticationPrincipal User userDetails, Model model) {
        User user = userService.findById(userDetails.getId());
        model.addAttribute("user", user);
        List<Order> orders = orderService.findByUserId(user.getId());
        model.addAttribute("orders", orders);
        return "my_orders";
    }

    @GetMapping("/reviews/my")
    public String getMyReviews(@AuthenticationPrincipal User userDetails, Model model) {
        User user = userService.findById(userDetails.getId());
        model.addAttribute("user", user);
        List<Review> reviews = reviewService.findByUserId(user.getId());
        model.addAttribute("reviews", reviews);
        return "my_reviews";
    }

    @GetMapping("/orders/incoming")
    public String getIncomingOrders(@AuthenticationPrincipal User userDetails, Model model) {
        User user = userService.findById(userDetails.getId());
        model.addAttribute("user", user);
        model.addAttribute("activeTab", "incoming"); // Для подсветки в сайдбаре

        // Используем новый метод сервиса
        List<Order> orders = orderService.findOrdersBySellerId(user.getId());
        model.addAttribute("orders", orders);

        return "seller_orders";
    }



    // POST запрос на смену статуса (работает и для продавца, и для покупателя)
    @PostMapping("/orders/status")
    public String updateOrderStatus(
            @AuthenticationPrincipal User userDetails,
            @RequestParam Long orderId,
            @RequestParam String newStatus,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request // Чтобы вернуться на ту же страницу
    ) {
        User user = userService.findById(userDetails.getId());

        try {
            orderService.changeStatus(orderId, user, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Статус заказа успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Возвращаемся на предыдущую страницу (чтобы работало и из my_orders, и из seller_orders)
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/profile");
    }
}