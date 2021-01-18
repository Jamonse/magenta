package com.jsoft.magenta.orders;

import com.jsoft.magenta.orders.domain.Order;
import com.jsoft.magenta.security.annotations.projects.ProjectManagePermission;
import com.jsoft.magenta.util.validation.PositiveNumber;
import com.jsoft.magenta.util.validation.ValidContent;
import com.jsoft.magenta.util.validation.ValidTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.jsoft.magenta.util.AppDefaults.*;

@Validated
@RestController
@RequestMapping("${application.url}orders")
@ProjectManagePermission
@RequiredArgsConstructor
public class OrderController
{
    private final OrderService orderService;

    private static final String DEFAULT_ORDER_SORT = "title";

    @PostMapping("{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(
            @PathVariable Long projectId,
            @RequestBody @Valid Order order
    )
    {
        return this.orderService.createOrder(projectId, order);
    }

    @PutMapping
    public Order updateOrder(
            @RequestBody @Valid Order order
    )
    {
        return this.orderService.updateOrder(order);
    }

    @PatchMapping("title/{orderId}")
    public Order updateOrderTitle(
            @PathVariable Long orderId,
            @RequestBody @ValidTitle String newTitle
    )
    {
        return orderService.updateOrderTitle(orderId, newTitle);
    }

    @PatchMapping("description/{orderId}")
    public Order updateOrderDescription(
            @PathVariable Long orderId,
            @RequestBody @ValidContent String newDescription
    )
    {
        return orderService.updateOrderDescription(orderId, newDescription);
    }

    @PatchMapping("amount/{orderId}")
    public Order updateOrderAmount(
            @PathVariable Long orderId,
            @RequestBody @PositiveNumber double newAmount
    )
    {
        return this.orderService.updateOrderAmount(orderId, newAmount);
    }

    @GetMapping("project/{projectId}")
    public Page<Order> getAllOrders(
            @PathVariable Long projectId,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_ORDER_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.orderService.getAllOrders(projectId, pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("{orderId}")
    public Order getOrder(@PathVariable Long orderId)
    {
        return this.orderService.getOrder(orderId);
    }

    @DeleteMapping("{orderId}")
    public void deleteOrder(@PathVariable Long orderId)
    {
        this.orderService.deleteOrder(orderId);
    }
}
