package org.steelboard.marketplace.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.steelboard.marketplace.dto.user.UserRegisterDto;
import org.steelboard.marketplace.exception.EmailAlreadyExistsException;
import org.steelboard.marketplace.exception.UsernameAlreadyExistsException;
import org.steelboard.marketplace.service.UserService;

@Controller
@AllArgsConstructor
public class AuthController {

    private final UserService userService; // ← Сервис вместо репозитория!

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserRegisterDto userDto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.register(userDto); // ← Делегируем сервису
            redirectAttributes.addFlashAttribute("success", "Registration successful!");
            return "redirect:/login";
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            result.rejectValue("username", "error.user.exists", e.getMessage());
            return "auth/register";
        }
    }
}
