package org.steelboard.marketplace.controller;// ... импорты

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.steelboard.marketplace.dto.user.UserRegisterDto;
import org.steelboard.marketplace.service.UserService;
// ...

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService; // Ваш сервис

    // --- ЛОГИН ---
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // --- РЕГИСТРАЦИЯ (Показ формы) ---
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegisterDto());
        return "auth/register";
    }

    // --- РЕГИСТРАЦИЯ (Обработка) ---
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserRegisterDto userDto,
                           BindingResult bindingResult,
                           Model model) {

        // 1. Проверка совпадения паролей
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Пароли не совпадают");
        }

        // 2. Проверка существования пользователя (пример)
        if (userService.findByUsername(userDto.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.user", "Такой логин уже занят");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register"; // Возвращаем форму с ошибками
        }

        // Сохранение (нужно написать метод в сервисе, принимающий DTO)
        userService.register(userDto);

        return "redirect:/login";
    }
}