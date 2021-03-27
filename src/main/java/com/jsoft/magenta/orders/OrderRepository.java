package com.jsoft.magenta.orders;

import com.jsoft.magenta.orders.domain.Order;
import com.jsoft.magenta.orders.domain.OrderSearchResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Long> findProjectIdById(Long orderId);

  Optional<Order> findByIdAndProjectAssociationsUserId(Long orderId, Long userId);

  Page<Order> findAllByProjectId(Long projectId, Pageable pageable);

  List<OrderSearchResult> findAllByProjectIdAndTitleContainingIgnoreCase(
      Long projectId, String titleExample, Pageable pageable);
}
