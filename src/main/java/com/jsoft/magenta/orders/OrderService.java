package com.jsoft.magenta.orders;

import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.orders.domain.Order;
import com.jsoft.magenta.orders.domain.OrderSearchResult;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.ProjectRepository;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.util.PageRequestBuilder;
import com.jsoft.magenta.util.WordFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService
{
    private final OrderRepository orderRepository;
    private final ProjectRepository projectRepository;

    private static final String DEFAULT_SORT_TYPE = "title";

    public Order createOrder(Long projectId, Order order)
    {
        Project project = findProject(projectId);
        order.setTitle(WordFormatter.capitalizeFormat(order.getTitle()));
        order.setCreatedAt(LocalDate.now());
        order.setProject(project);
        return this.orderRepository.save(order);
    }

    public Order updateOrder(Order order)
    {
        Order orderToUpdate = findOrder(order.getId());
        orderToUpdate.setTitle(WordFormatter.capitalizeFormat(order.getTitle()));
        orderToUpdate.setDescription(order.getDescription());
        orderToUpdate.setAmount(order.getAmount());
        return this.orderRepository.save(orderToUpdate);
    }

    public Order updateOrderTitle(Long orderId, String newTitle)
    {
        Order order = findOrder(orderId);
        order.setTitle(WordFormatter.capitalizeFormat(newTitle));
        return this.orderRepository.save(order);
    }

    public Order updateOrderDescription(Long orderId, String newDescription)
    {
        Order order = findOrder(orderId);
        order.setDescription(newDescription);
        return this.orderRepository.save(order);
    }

    public Order updateOrderAmount(Long orderId, double newAmount)
    {
        Order order = findOrder(orderId);
        order.setAmount(newAmount);
        return this.orderRepository.save(order);
    }

    public Page<Order> getAllOrders(
            Long projectId, int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        findProject(projectId);
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Order> pageResult = this.orderRepository.findAllByProjectId(projectId, pageRequest);
        return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
    }

    public List<OrderSearchResult> getOrdersResults(
            Long projectId, String example, int resultsCount)
    {
        findProject(projectId);
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
                0, resultsCount, DEFAULT_SORT_TYPE, true);
        return this.orderRepository
                .findAllByProjectIdAndTitleContainingIgnoreCase(projectId, example, pageRequest);
    }

    public Order getOrder(Long orderId)
    {
        return findOrder(orderId);
    }

    public void deleteOrder(Long orderId)
    {
        findOrder(orderId);
        this.orderRepository.deleteById(orderId);
    }

    private Order findOrder(Long orderId)
    {
        Long userId = UserEvaluator.currentUserId();
        return this.orderRepository
                .findByIdAndProjectAssociationsUserId(orderId, userId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
    }

    private Project findProject(Long projectId)
    {
        Long userId = UserEvaluator.currentUserId();
        return this.projectRepository
                .findByIdAndAssociationsUserId(projectId, userId)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));
    }

}
