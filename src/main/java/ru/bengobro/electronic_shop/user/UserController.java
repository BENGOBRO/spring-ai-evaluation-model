package ru.bengobro.electronic_shop.user;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bengobro.electronic_shop.order.OrderResponse;
import ru.bengobro.electronic_shop.order.OrderService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return UserResponse.from(userService.getById(id));
    }

    @GetMapping("/{id}/orders")
    public List<OrderResponse> orders(@PathVariable Long id) {
        // убедимся, что пользователь существует (иначе 404)
        userService.getById(id);
        return orderService.getByUser(id).stream().map(OrderResponse::from).toList();
    }
}
