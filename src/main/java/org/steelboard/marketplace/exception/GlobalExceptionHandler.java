package org.steelboard.marketplace.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


// дописать обработчики
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(ProductNotFoundException.class)
    public String handleProductNotFound(ProductNotFoundException e,
                                        RedirectAttributes redirectAttributes) {
        log.warn("Finding product failed: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "index";
    }

    // Валидация форм (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException e, Model model) {
        log.warn("Validation failed: {}", e.getMessage());
        model.addAttribute("errors", e.getBindingResult().getAllErrors());
        return "auth/register"; // или другая форма
    }
}
