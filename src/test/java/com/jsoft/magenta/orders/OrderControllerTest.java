package com.jsoft.magenta.orders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jsoft.magenta.orders.domain.Order;
import com.jsoft.magenta.util.Stringify;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@WithUserDetails("admin@admin.com")
@AutoConfigureMockMvc
public class OrderControllerTest {

  @MockBean
  private OrderService orderService;

  @Autowired
  private OrderController orderController;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create order")
  public void createOrder() throws Exception {
    Order order = new Order();
    order.setTitle("title");
    order.setDescription("description");
    order.setAmount(10);
    Order returned = new Order();
    returned.setId(1L);
    returned.setTitle("title");
    returned.setDescription("description");
    returned.setAmount(10);
    returned.setCreatedAt(LocalDate.now());

    Mockito.when(orderService.createOrder(1L, order)).thenReturn(returned);

    mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "orders/{projectId}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(Stringify.asJsonString(order)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(returned.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  @DisplayName("Create order without title - should throw exception")
  public void createOrderWithoutTitle() throws Exception {
    Order order = new Order();
    order.setDescription("description");
    order.setAmount(10);
    Order returned = new Order();
    returned.setId(1L);
    returned.setDescription("description");
    returned.setAmount(10);
    returned.setCreatedAt(LocalDate.now());

    Mockito.when(orderService.createOrder(1L, order)).thenReturn(returned);

    mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "orders/{projectId}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(Stringify.asJsonString(order)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @DisplayName("Create order with invalid title - should throw exception")
  public void createOrderWithInvalidTitle() throws Exception {
    Order order = new Order();
    order.setTitle("t");
    order.setDescription("description");
    order.setAmount(10);
    Order returned = new Order();
    returned.setId(1L);
    returned.setTitle("t");
    returned.setDescription("description");
    returned.setAmount(10);
    returned.setCreatedAt(LocalDate.now());

    Mockito.when(orderService.createOrder(1L, order)).thenReturn(returned);

    mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "orders/{projectId}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(Stringify.asJsonString(order)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @DisplayName("Create order with invalid amount - should throw exception")
  public void createOrderWithInvalidAmount() throws Exception {
    Order order = new Order();
    order.setTitle("title");
    order.setDescription("description");
    order.setAmount(-1);
    Order returned = new Order();
    returned.setDescription("description");
    returned.setAmount(-1);
    returned.setCreatedAt(LocalDate.now());

    Mockito.when(orderService.createOrder(1L, order)).thenReturn(returned);

    mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "orders/{projectId}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(Stringify.asJsonString(order)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers
            .jsonPath("$.message")
            .value("Error/s during parameters validation")
        );
  }

  @Test
  @DisplayName("Update order")
  public void updateOrder() throws Exception {
    Order order = new Order();
    order.setId(1L);
    order.setTitle("title");
    order.setCreatedAt(LocalDate.now());
    order.setAmount(10);
    order.setDescription("description");

    Mockito.when(orderService.updateOrder(order)).thenReturn(order);

    mockMvc.perform(MockMvcRequestBuilders.put(Stringify.BASE_URL + "orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(Stringify.asJsonString(order)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(order.getTitle()));
  }

  @Test
  @DisplayName("Update order title")
  public void updateOrderTitle() throws Exception {
    Order order = new Order();
    order.setId(1L);
    order.setTitle("new title");
    String orderTitle = "new title";

    Mockito.when(orderService.updateOrderTitle(1L, "new title")).thenReturn(order);

    mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "orders/title/{orderId}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(orderTitle))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("new title"));
  }

  @Test
  @DisplayName("Update order title with invalid title - should throw exception")
  public void updateOrderInvalidTitle() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "orders/title/{orderId}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(""))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @DisplayName("Update order description")
  public void updateOrderDescription() throws Exception {
    Order order = new Order();
    order.setId(1L);
    order.setDescription("new description");
    String orderDescription = "new description";

    Mockito.when(orderService.updateOrderDescription(1L, "new description")).thenReturn(order);

    mockMvc.perform(
        MockMvcRequestBuilders.patch(Stringify.BASE_URL + "orders/description/{orderId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(orderDescription))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("new description"));
  }

  @Test
  @DisplayName("Update order amount")
  public void updateOrderAmount() throws Exception {
    Order order = new Order();
    order.setId(1L);
    order.setAmount(10);

    Mockito.when(orderService.updateOrderAmount(1L, 10))
        .thenReturn(order);

    mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "orders/amount/{orderId}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content("10"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value("10.0"));
  }

  @Test
  @DisplayName("Update order amount with negative number - should throw exception")
  public void updateOrderAmountWithNegativeNumber() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "orders/amount/{orderId}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content("-1"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @DisplayName("Get all orders")
  public void getAllOrders() throws Exception {

    Sort sort = Sort.by("title").ascending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);

    when(orderService.getAllOrders(1L, 0, 5, "title", true))
        .thenReturn(new PageImpl<>(List.of(new Order()), pageRequest, 1));

    mockMvc.perform(get(Stringify.BASE_URL + "orders/project/{orderId}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("pageIndex", "0")
        .queryParam("pageSize", "5")
        .queryParam("sortBy", "title")
        .queryParam("asc", "true"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("Get order")
  public void getOrder() throws Exception {
    Order order = new Order();
    order.setId(1L);
    order.setTitle("title");

    when(orderService.getOrder(order.getId())).thenReturn(order);

    mockMvc.perform(get(Stringify.BASE_URL + "orders/{orderId}", order.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(Stringify.asJsonString(order)));
  }

  @Test
  @DisplayName("Delete order")
  public void deleteOrder() throws Exception {
    doNothing().when(orderService).deleteOrder(1L);

    mockMvc.perform(delete(Stringify.BASE_URL + "orders/{orderId}", 1L)
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

}
