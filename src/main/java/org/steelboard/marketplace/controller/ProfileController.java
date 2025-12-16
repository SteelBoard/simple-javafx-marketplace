package org.steelboard.marketplace.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.steelboard.marketplace.dto.user.UserUpdateDto;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.UserService;

@AllArgsConstructor
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String getProfile(@AuthenticationPrincipal User userDetails, Model model) {
        User user  = userService.findById(userDetails.getId());
        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        model.addAttribute("userUpdateDto", dto);
        return "profile";
    }

    @PostMapping
    public String updateProfile(
            @AuthenticationPrincipal User userDetails,
            @Valid @ModelAttribute("userUpdateDto") UserUpdateDto dto,
            BindingResult bindingResult,
            Model model
    ) {
        // Проверяем совпадение пароля только если введён
        User user = userService.findById(userDetails.getId());
        if (dto.isPasswordBeingUpdated() && !dto.getPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.userUpdateDto", "Passwords do not match");
        }

        // Проверка username на уникальность, если изменился
        if (!user.getUsername().equals(dto.getUsername()) && userService.usernameExists(dto.getUsername())) {
            bindingResult.rejectValue("username", "error.userUpdateDto", "Username is already taken");
        }

        if (bindingResult.hasErrors()) {
            return "profile";
        }

        userService.updateUser(user, dto);
        model.addAttribute("successMessage", "Profile updated successfully");
        return "profile";
    }
}
