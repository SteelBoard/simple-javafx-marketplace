package org.steelboard.marketplace.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.UserService;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // Форма редактирования конкретного пользователя
    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/user_edit";
    }

    // Сохранение изменений (только active)
    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id, @RequestParam boolean active) {
        userService.updateActiveStatus(id, active);
        return "redirect:/admin/users";
    }
}
