package com.jsoft.magenta.util.pagination;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;

@Getter
public class PageResponse<T> {

  private final List<T> content;
  private final long totalElements;
  private final int pageIndex;
  private final int pageSize;
  private final String sortBy;
  private final boolean sortDirection;

  public PageResponse(List<T> content, long totalElements, Pageable pageable) {
    this.content = content;
    this.totalElements = totalElements;
    this.pageIndex = pageable.getPageNumber();
    this.pageSize = pageable.getPageSize();
    Order sortOrder = pageable.getSort().get().findFirst().orElse(null);
    if (sortOrder != null) {
      this.sortBy = sortOrder.getProperty();
      this.sortDirection = sortOrder.getDirection().isAscending();
    } else {
      this.sortBy = null;
      this.sortDirection = false;
    }
  }
}
