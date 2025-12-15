package org.steelboard.marketplace.controller.admin;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.CartService;
import org.steelboard.marketplace.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private UserService userService;
    private CartService cartService;

    @GetMapping
    public String users(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,   // ← ВОТ ОН
            @RequestParam(required = false) String q,
            Model model
    ) {

        Page<User> usersPage = (q == null || q.isBlank())
                ? userService.findAll(page, size)
                : userService.search(q.trim(), page, size);

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("size", size);   // ← прокидываем в thymeleaf
        model.addAttribute("q", q);

        return "admin/users";
    }

    @GetMapping("/{id}")
    public String user(Model model,
                       @PathVariable Long id) {

        model.addAttribute("user", userService.findById(id));
        return "admin/user_details";
    }

    @PostMapping("/{id}/active")
    public String updateActive(@RequestParam(required = false) Boolean active,
                               @PathVariable Long id,
                               Model model) {
        boolean isActive = active != null && active;
        userService.updateActive(id, isActive);
        model.addAttribute("user", userService.findById(id));
        return "redirect:/admin/users/" + id;
    }

    @GetMapping("/{id}/cart")
    public String cart(Model model,
                       @PathVariable Long id) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("cartItems", user.getCart().getCartItems());
        return "admin/user_cart";
    }
}
