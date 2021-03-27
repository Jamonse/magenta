package com.jsoft.magenta.orders;

import com.jsoft.magenta.events.projects.ProjectRelatedEntityEvent;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.orders.domain.Order;
import com.jsoft.magenta.orders.domain.OrderSearchResult;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.util.PageRequestBuilder;
import com.jsoft.magenta.util.WordFormatter;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final SecurityService securityService;

  private static final String DEFAULT_SORT_TYPE = "title";

  public Order createOrder(Long projectId, Order order) {
    this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
    order.setTitle(WordFormatter.capitalizeFormat(order.getTitle()));
    order.setCreatedAt(LocalDate.now());
    order.setProject(new Project(projectId));
    return this.orderRepository.save(order);
  }

  public Order updateOrder(Order order) {
    validateProjectAndPermission(order.getId());
    Order orderToUpdate = findOrder(order.getId());
    orderToUpdate.setTitle(WordFormatter.capitalizeFormat(order.getTitle()));
    orderToUpdate.setDescription(order.getDescription());
    orderToUpdate.setAmount(order.getAmount());
    return this.orderRepository.save(orderToUpdate);
  }

  public Order updateOrderTitle(Long orderId, String newTitle) {
    validateProjectAndPermission(orderId);
    Order order = findOrder(orderId);
    order.setTitle(WordFormatter.capitalizeFormat(newTitle));
    return this.orderRepository.save(order);
  }

  public Order updateOrderDescription(Long orderId, String newDescription) {
    validateProjectAndPermission(orderId);
    Order order = findOrder(orderId);
    order.setDescription(newDescription);
    return this.orderRepository.save(order);
  }

  public Order updateOrderAmount(Long orderId, double newAmount) {
    validateProjectAndPermission(orderId);
    Order order = findOrder(orderId);
    order.setAmount(newAmount);
    return this.orderRepository.save(order);
  }

  public Page<Order> getAllOrders(
      Long projectId, int pageIndex, int pageSize, String sortBy, boolean asc) {
    this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Page<Order> pageResult = this.orderRepository.findAllByProjectId(projectId, pageRequest);
    return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
  }

  public List<OrderSearchResult> getOrdersResults(
      Long projectId, String example, int resultsCount) {
    this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        0, resultsCount, DEFAULT_SORT_TYPE, true);
    return this.orderRepository
        .findAllByProjectIdAndTitleContainingIgnoreCase(projectId, example, pageRequest);
  }

  public Order getOrder(Long orderId) {
    validateProjectAndPermission(orderId);
    return findOrder(orderId);
  }

  public void deleteOrder(Long orderId) {
    isOrderExists(orderId);
    validateProjectAndPermission(orderId);
    this.orderRepository.deleteById(orderId);
  }

  private Order findOrder(Long orderId) {
    Long userId = securityService.currentUserId();
    return this.orderRepository
        .findByIdAndProjectAssociationsUserId(orderId, userId)
        .orElseThrow(() -> new NoSuchElementException("Order not found"));
  }

  private Long findProjectId(Long orderId) {
    return this.orderRepository
        .findProjectIdById(orderId)
        .orElseThrow(() -> new NoSuchElementException("Project not found"));
  }

  private void isOrderExists(Long orderId) {
    boolean exists = this.orderRepository.existsById(orderId);
    if (!exists) {
      throw new NoSuchElementException("Order not found");
    }
  }

  private void validateProjectAndPermission(Long orderId) {
    Long projectId = findProjectId(orderId);
    this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
  }

}
