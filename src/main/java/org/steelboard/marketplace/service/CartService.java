package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.CartItem;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.exception.CartItemNotFoundException;
import org.steelboard.marketplace.repository.CartItemRepository;
import org.steelboard.marketplace.repository.CartRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Transactional
public class CartService {

    CartItemRepository cartItemRepository;
    CartRepository cartRepository;
    ProductService productService;
    UserService userService;

    public void addProductToCart(Long productId, String username) {
        Cart cart = getCartByUsername(username);
        Optional<CartItem> cartItemOptional = cartItemRepository.findByProduct_IdAndCart(productId, cart);
        CartItem item;

        if (cartItemOptional.isPresent()) {

            item = cartItemOptional.get();
            item.setQuantity(item.getQuantity() + 1);
            item.setUnitPrice(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            cartItemRepository.save(item);
        }
        else {
            Product itemProduct = productService.getProduct(productId);
            item = new CartItem(cart, itemProduct);
            cartItemRepository.save(item);
        }
    }

    public void updateCart(Cart cart) {
        cartRepository.save(cart);
    }

    public void updateQuantity(String username, Long productId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByProduct_IdAndCart(productId, getCartByUsername(username))
                .orElseThrow(() -> new CartItemNotFoundException(productId,  username));
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItemRepository.save(cartItem);
    }

    public void removeItem(String username, Long productId) {
        cartItemRepository.removeByCartAndProduct_Id(getCartByUsername(username), productId);
    }

    @Transactional(readOnly = true)
    public Cart getCartByUsername(String username) {
        return userService.findByUsername(username).getCart();
    }

    public void clearCartByUsername(String username) {
        cartItemRepository.removeByCart(getCartByUsername(username));
    }

    public void removeItemsFromCart(Cart cart, List<Product> products) {
        cartItemRepository.deleteByCartAndProductIdsIn(cart.getId(), products.stream().map(Product::getId).toList());
    }
}
