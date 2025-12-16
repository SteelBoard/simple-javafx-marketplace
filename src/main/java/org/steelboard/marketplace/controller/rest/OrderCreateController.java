package org.steelboard.marketplace.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.OrderService;
import org.steelboard.marketplace.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderCreateController {

    private final OrderService orderService;
    private final ObjectMapper mapper;
    private final UserService userService;

    @PostMapping("/order/create")
    public String createOrder(@AuthenticationPrincipal User userDetails,
                              @RequestParam String selectedProductIds) throws Exception {

        List<Long> productIds = mapper.readValue(
                selectedProductIds,
                mapper.getTypeFactory().constructCollectionType(List.class, Long.class)
        );

        User user = userService.findById(userDetails.getId());
        var order = orderService.createOrder(user, productIds);

        return "redirect:/order/" + order.getId();
    }
}
