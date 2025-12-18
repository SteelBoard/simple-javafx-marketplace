package org.steelboard.marketplace.controller.admin;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.Review;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.ProductService;
import org.steelboard.marketplace.service.ReviewService;
import org.steelboard.marketplace.service.UserService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final ProductService productService;
    private final ReviewService reviewService;
    private UserService userService;

    @GetMapping
    public String users(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,   
            @RequestParam(required = false) String q,
            Model model
    ) {

        Page<User> usersPage = (q == null || q.isBlank())
                ? userService.findAll(page, size)
                : userService.search(q.trim(), page, size);

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("size", size);   
        model.addAttribute("q", q);

        return "admin/user/users";
    }

    @GetMapping("/{id}")
    public String user(Model model,
                       @PathVariable Long id) {

        model.addAttribute("user", userService.findById(id));
        return "admin/user/user_details";
    }

    @PostMapping("/{id}/active")
    public String updateActive(@RequestParam(required = false) Boolean active,
                               @PathVariable Long id,
                               Model model) {
        boolean isActive = active != null && active;
        userService.updateActive(id, isActive);
        model.addAttribute("user", userService.findById(id));
        return "redirect:/admin/users/" + id;
    }

    @GetMapping("/{id}/cart")
    public String cart(Model model,
                       @PathVariable Long id) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("cartItems", user.getCart().getCartItems());
        return "admin/user/user_cart";
    }

    @GetMapping("/{id}/products")
    public String userProducts(
            @PathVariable Long id,
            Model model
    ) {
        User user = userService.findById(id);
        List<Product> products = productService.findBySellerId(id);

        model.addAttribute("user", user);
        model.addAttribute("products", products);

        return "admin/user/user_products";
    }

    @GetMapping("/{id}/reviews")
    public String userReviews(
            @PathVariable Long id,
            Model model
    ) {
        User user = userService.findById(id);
        List<Review> reviews = reviewService.findByUserId(id);

        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);

        return "admin/user/user_reviews";
    }
}
