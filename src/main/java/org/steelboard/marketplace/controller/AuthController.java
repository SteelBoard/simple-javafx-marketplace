package org.steelboard.marketplace.controller;

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


@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService; 

    
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegisterDto());
        return "auth/register";
    }

    
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserRegisterDto userDto,
                           BindingResult bindingResult,
                           Model model) {

        
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Пароли не совпадают");
        }

        
        if (userService.findByUsername(userDto.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.user", "Такой логин уже занят");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register"; 
        }

        
        userService.register(userDto);

        return "redirect:/login";
    }
}