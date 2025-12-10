package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.CartItem;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.exception.ProductNotFoundException;
import org.steelboard.marketplace.repository.CartItemRepository;

import java.util.Objects;

@AllArgsConstructor
@Service
public class CartService {

    CartItemRepository cartItemRepository;
    ProductService productService;
    UserService userService;

    @Transactional
    public void addProductToCart(Long productId, String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        Cart cart = user.getCart();

        Product currentProduct = productService.getProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        CartItem item;

        if (cart.getCartItems().stream()
                .map(CartItem::getProduct)
                .anyMatch(product -> Objects.equals(product.getId(), currentProduct.getId()))) {

            item = cartItemRepository.findByProductAndCart(currentProduct, cart);
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        }
        else {
            item = new CartItem(cart,  currentProduct);
            cartItemRepository.save(item);
        }
    }

    @Transactional
    public void updateQuantity(String username, Long productId, Integer quantity) {
        CartItem cartItem = getCartByUsername(username).getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().get();
        cartItem.setQuantity(quantity);
    }

    @Transactional
    public void removeItem(String username, Long productId) {
        cartItemRepository.removeByCartAndProduct_Id(getCartByUsername(username), productId);
    }

    @Transactional(readOnly = true)
    public Cart getCartByUsername(String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return user.getCart();
    }
}
