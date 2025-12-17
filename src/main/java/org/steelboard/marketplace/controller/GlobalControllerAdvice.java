package org.steelboard.marketplace.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.repository.UserRepository;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserRepository userRepository;

    @ModelAttribute("cartItemCount")
    public int populateCartItemCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Если пользователь не залогинен (аноним), корзина пуста (или 0)
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return 0;
        }

        String username = authentication.getName();
        // Находим юзера и его корзину
        // Важно: в User.java у вас cart FetchType.LAZY (по дефолту для OneToOne),
        // но Cart.java имеет cartItems EAGER, так что size() сработает.
        return userRepository.findByUsername(username)
                .map(User::getCart)
                .map(cart -> cart.getCartItems().size()) // Считаем количество уникальных позиций
                .orElse(0);
    }

    @ModelAttribute("currentUser")
    public User populateCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName()).orElse(null);
    }
}