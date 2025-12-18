package org.steelboard.marketplace.controller.admin;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.steelboard.marketplace.entity.*;
import org.steelboard.marketplace.service.OrderService;
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
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping
    public String users(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String q,
            Model model
    ) {
        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<User> usersPage = (q == null || q.isBlank())
                ? userService.findAll(pageable)
                : userService.search(q.trim(), pageable);

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("q", q);

        return "admin/user/users";
    }

    @GetMapping("/{id}")
    public String user(Model model, @PathVariable Long id) {
        model.addAttribute("user", userService.findById(id));
        return "admin/user/user_details";
    }

    @PostMapping("/{id}/active")
    public String updateActive(@RequestParam(required = false) Boolean active,
                               @PathVariable Long id,
                               RedirectAttributes redirectAttributes) { // Добавили RedirectAttributes для сообщений

        User user = userService.findById(id);

        // ПРОВЕРКА: Если пользователь Админ — запрещаем менять активность
        if (user.isAdmin()) {
            // Можно добавить сообщение об ошибке, если хотите
            return "redirect:/admin/users/" + id + "?error=admin_change_denied";
        }

        boolean isActive = active != null && active;
        userService.updateActive(id, isActive);

        return "redirect:/admin/users/" + id;
    }

    @GetMapping("/{id}/cart")
    public String cart(Model model, @PathVariable Long id) {
        User user = userService.findById(id);
        List<CartItem> cartItems = user.getCart().getCartItems().stream().toList();
        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        return "admin/user/user_cart";
    }

    @GetMapping("/{id}/orders")
    public String userOrders(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort, // Сортировка по дате по умолчанию
            @RequestParam(defaultValue = "desc") String dir,       // Сначала новые
            @RequestParam(required = false) String search,
            Model model
    ) {
        User user = userService.findById(id);

        Sort.Direction direction = dir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<Order> ordersPage = orderService.getUserOrders(id, search, pageable);

        model.addAttribute("user", user);
        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);
        model.addAttribute("search", search);

        return "admin/user/user_orders";
    }

    // --- ИСПРАВЛЕННЫЙ МЕТОД ТОВАРОВ ---
    @GetMapping("/{id}/products")
    public String userProducts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(required = false) String search,
            Model model
    ) {
        User user = userService.findById(id);
        Sort.Direction direction = dir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        // ТЕПЕРЬ ПОИСК РАБОТАЕТ ЧЕРЕЗ СЕРВИС И БАЗУ ДАННЫХ
        Page<Product> productsPage = productService.findBySellerId(id, search, pageable);

        model.addAttribute("user", user);
        model.addAttribute("productsPage", productsPage);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);
        model.addAttribute("search", search);

        return "admin/user/user_products";
    }

    // --- ИСПРАВЛЕННЫЙ МЕТОД ОТЗЫВОВ ---
    @GetMapping("/{id}/reviews")
    public String userReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(required = false) String search,
            Model model
    ) {
        User user = userService.findById(id);
        Sort.Direction direction = dir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        // ТЕПЕРЬ ПОИСК РАБОТАЕТ ЧЕРЕЗ СЕРВИС И БАЗУ ДАННЫХ
        Page<Review> reviewsPage = reviewService.findByUserId(id, search, pageable);

        model.addAttribute("user", user);
        model.addAttribute("reviewsPage", reviewsPage);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);
        model.addAttribute("search", search);

        return "admin/user/user_reviews";
    }
}