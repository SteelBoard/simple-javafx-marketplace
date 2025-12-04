package org.steelboard.marketplace.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Обработка твоих бизнес-ошибокS
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public String handleUsernameExists(UsernameAlreadyExistsException e,
                                       RedirectAttributes redirectAttributes) {
        log.warn("Registration failed: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailExists(EmailAlreadyExistsException e,
                                    RedirectAttributes redirectAttributes) {
        log.warn("Registration failed: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/register";
    }

    // Валидация форм (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException e, Model model) {
        log.warn("Validation failed: {}", e.getMessage());
        model.addAttribute("errors", e.getBindingResult().getAllErrors());
        return "auth/register"; // или другая форма
    }
}
