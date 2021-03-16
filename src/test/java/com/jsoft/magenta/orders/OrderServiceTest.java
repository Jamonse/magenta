package com.jsoft.magenta.orders;

import com.jsoft.magenta.orders.domain.Order;
import com.jsoft.magenta.orders.domain.OrderSearchResult;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.ProjectRepository;
import com.jsoft.magenta.security.SecurityService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class OrderServiceTest
{
    @InjectMocks
    private OrderService orderService;

    @Mock
    private SecurityService securityService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    private void init()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Save order")
    public void saveOrder()
    {
        Order order = new Order();
        order.setTitle("order");
        Order returnedOrder = new Order();
        returnedOrder.setTitle("order");
        returnedOrder.setId(1L);

        when(orderRepository.save(order)).thenReturn(returnedOrder);
        when(securityService.currentUserId()).thenReturn(1L);
        
        this.orderService.createOrder(1L, order);

        Assertions.assertTrue(order.getTitle().startsWith("O"));
        Assertions.assertNotNull(order.getCreatedAt());
        Assertions.assertNotNull(order.getProject());
    }

    @Test
    @DisplayName("Update order")
    public void updateOrder()
    {
        Order order = new Order();
        order.setTitle("order");
        order.setId(1L);
        Order returnedOrder = new Order();
        returnedOrder.setTitle("title");
        returnedOrder.setId(1L);

        when(orderRepository.save(order)).thenReturn(returnedOrder);
        when(orderRepository.findByIdAndProjectAssociationsUserId(1L, 1L))
                .thenReturn(Optional.of(new Order()));
        when(securityService.currentUserId()).thenReturn(1L);
        when(orderRepository.findProjectIdById(1L)).thenReturn(Optional.of(1L));

        this.orderService.updateOrder(order);

        Assertions.assertNotEquals(order.getTitle(), returnedOrder.getTitle());
    }

    @Test
    @DisplayName("Update order title")
    public void updateOrderTitle()
    {
        Order order = new Order();
        order.setTitle("new title");
        order.setId(1L);
        Order returnedOrder = new Order();
        returnedOrder.setTitle("New title");
        returnedOrder.setId(1L);

        when(orderRepository.save(order)).thenReturn(returnedOrder);
        when(orderRepository.findByIdAndProjectAssociationsUserId(1L, 1L))
                .thenReturn(Optional.of(order));
        when(securityService.currentUserId()).thenReturn(1L);
        when(orderRepository.findProjectIdById(1L)).thenReturn(Optional.of(1L));

        Order updatedOrder = this.orderService.updateOrderTitle(1L, "new title");

        Assertions.assertEquals(updatedOrder, returnedOrder);
        Assertions.assertNotNull(updatedOrder.getId());
    }

    @Test
    @DisplayName("Update order description")
    public void updateOrderDescription()
    {
        Order order = new Order();
        order.setTitle("new title");
        order.setDescription("new description");
        order.setId(1L);
        Order returnedOrder = new Order();
        returnedOrder.setTitle("new title");
        returnedOrder.setDescription("new description");
        returnedOrder.setId(1L);

        when(orderRepository.save(order)).thenReturn(returnedOrder);
        when(orderRepository.findByIdAndProjectAssociationsUserId(1L, 1L))
                .thenReturn(Optional.of(order));
        when(securityService.currentUserId()).thenReturn(1L);
        when(orderRepository.findProjectIdById(1L)).thenReturn(Optional.of(1L));

        Order updatedOrder = this.orderService.updateOrderDescription(1L, "new description");

        Assertions.assertEquals(updatedOrder, returnedOrder);
        Assertions.assertNotNull(updatedOrder.getId());
    }

    @Test
    @DisplayName("Update order amount")
    public void updateOrderAmount()
    {
        Order order = new Order();
        order.setTitle("new title");
        order.setDescription("new description");
        order.setAmount(10);
        order.setId(1L);
        Order returnedOrder = new Order();
        returnedOrder.setTitle("new title");
        returnedOrder.setDescription("new description");
        returnedOrder.setAmount(10);
        returnedOrder.setId(1L);

        when(orderRepository.save(order)).thenReturn(returnedOrder);
        when(orderRepository.findByIdAndProjectAssociationsUserId(1L, 1L))
                .thenReturn(Optional.of(order));
        when(securityService.currentUserId()).thenReturn(1L);
        when(orderRepository.findProjectIdById(1L)).thenReturn(Optional.of(1L));

        Order updatedOrder = this.orderService.updateOrderAmount(1L, 10);

        Assertions.assertEquals(updatedOrder, returnedOrder);
        Assertions.assertNotNull(updatedOrder.getId());
    }

    @Test
    @DisplayName("Get all project orders")
    public void getAllOrders()
    {
        Order order = new Order();
        order.setTitle("title");
        order.setDescription("description");
        order.setAmount(10);
        order.setId(1L);

        List<Order> orders = List.of(order);
        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 1, sort);

        when(orderRepository.findAllByProjectId(1L, pageRequest))
                .thenReturn(new PageImpl<>(orders, pageRequest, 1));
        when(securityService.currentUserId()).thenReturn(1L);

        Page<Order> orderPage = this.orderService.getAllOrders(1L, 0, 1, "title", true);

        Assertions.assertFalse(orderPage.isEmpty());
        Assertions.assertEquals(orderPage.getContent().size(), 1);

        verify(orderRepository).findAllByProjectId(1L, pageRequest);
    }

    @Test
    @DisplayName("Get order")
    public void getOrder()
    {
        Order order = new Order();
        order.setTitle("title");
        order.setDescription("description");
        order.setAmount(10);
        order.setId(1L);

        when(orderRepository.findByIdAndProjectAssociationsUserId(1L, 1L))
                .thenReturn(Optional.of(order));
        when(securityService.currentUserId()).thenReturn(1L);
        when(orderRepository.findProjectIdById(1L)).thenReturn(Optional.of(1L));

        Order o = this.orderService.getOrder(1L);

        Assertions.assertNotNull(o);
        Assertions.assertEquals(o, order);

        verify(orderRepository).findByIdAndProjectAssociationsUserId(1L, 1L);
    }

    @Test
    @DisplayName("Get orders by example")
    public void getOrdersByExample()
    {
        OrderSearchResult order = new OrderSearchResult() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getTitle() {
                return "title";
            }
        };

        List<OrderSearchResult> orders = List.of(order);
        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 1, sort);

        when(orderRepository.findAllByProjectIdAndTitleContainingIgnoreCase(1L, "t", pageRequest))
                .thenReturn(orders);
        when(securityService.currentUserId()).thenReturn(1L);

        List<OrderSearchResult> orderSearchResults = this.orderService
                .getOrdersResults(1L, "t", 1);

        Assertions.assertFalse(orderSearchResults.isEmpty());
        Assertions.assertEquals(orderSearchResults.size(), 1);

        verify(orderRepository).findAllByProjectIdAndTitleContainingIgnoreCase(1L, "t", pageRequest);
    }

    @Test
    @DisplayName("Delete order")
    public void deleteOrder()
    {
        Order order = new Order();
        order.setTitle("title");
        order.setDescription("description");
        order.setAmount(10);
        order.setId(1L);

        when(orderRepository.findProjectIdById(1L)).thenReturn(Optional.of(1L));
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);
        when(securityService.currentUserId()).thenReturn(1L);

        this.orderService.deleteOrder(1L);

        verify(orderRepository).deleteById(1L);
        verify(orderRepository).existsById(1L);
    }


}
