package com.jsoft.magenta.orders;

import com.jsoft.magenta.orders.domain.Order;
import com.jsoft.magenta.projects.domain.Project;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OrderPojoTest {

  @Test
  @DisplayName("Create two orders and check getter and equals")
  public void create() {
    Order order1 = new Order(1L, "title", "description", LocalDate.now(), new Project(), 10);
    Order order2 = new Order(1L, "title", "description", LocalDate.now(), new Project(), 10);

    Assertions.assertEquals(order1.getTitle(), "title");
    Assertions.assertEquals(order1, order2);
  }

  @Test
  @DisplayName("Update order and check getter")
  public void update() {
    Order order1 = new Order(1L, "title", "description", LocalDate.now(), new Project(), 10);
    order1.setTitle("new title");

    Assertions.assertEquals(order1.getTitle(), "new title");
  }
}
