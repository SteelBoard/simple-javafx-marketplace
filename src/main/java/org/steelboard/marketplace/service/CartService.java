package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.CartItem;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.exception.CartItemNotFoundException;
import org.steelboard.marketplace.repository.CartItemRepository;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CartService {

    CartItemRepository cartItemRepository;
    ProductService productService;
    UserService userService;

    @Transactional
    public void addProductToCart(Long productId, String username) {
        Cart cart = getCartByUsername(username);
        Optional<CartItem> cartItemOptional = cartItemRepository.findByProduct_IdAndCart(productId, cart);
        CartItem item;

        if (cartItemOptional.isPresent()) {

            item = cartItemOptional.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        }
        else {
            Product itemProduct = productService.getProduct(productId);
            item = new CartItem(cart, itemProduct);
            cartItemRepository.save(item);
        }
    }

    @Transactional
    public void updateQuantity(String username, Long productId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByProduct_IdAndCart(productId, getCartByUsername(username))
                .orElseThrow(() -> new CartItemNotFoundException(productId,  username));
        cartItem.setQuantity(quantity);
    }

    @Transactional
    public void removeItem(String username, Long productId) {
        cartItemRepository.removeByCartAndProduct_Id(getCartByUsername(username), productId);
    }

    @Transactional(readOnly = true)
    public Cart getCartByUsername(String username) {
        return userService.findByUsername(username).getCart();
    }

    @Transactional
    public void clearCartByUsername(String username) {
        cartItemRepository.removeByCart(getCartByUsername(username));
    }
}
