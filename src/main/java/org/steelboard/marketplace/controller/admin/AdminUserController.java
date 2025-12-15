package org.steelboard.marketplace.controller.admin;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private UserService userService;

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
}
