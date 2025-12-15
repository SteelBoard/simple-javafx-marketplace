package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.CartItem;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.User;
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

    public void addProductToCart(Long productId, User user) {
        Cart cart = user.getCart();
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

    public void addCartByUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        cartRepository.save(cart);
    }

    public void updateQuantity(User user, Long productId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByProduct_IdAndCart(productId, user.getCart())
                .orElseThrow(() -> new CartItemNotFoundException(productId,  user.getUsername()));
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItemRepository.save(cartItem);
    }

    public void removeItem(User user, Long productId) {
        cartItemRepository.removeByCartAndProduct_Id(user.getCart(), productId);
    }

    public void clearCartByUser(User user) {
        cartItemRepository.removeByCart(user.getCart());
    }

    public void removeItemsFromCart(Cart cart, List<Product> products) {
        cartItemRepository.deleteByCartAndProductIdsIn(cart.getId(), products.stream().map(Product::getId).toList());
    }
}
