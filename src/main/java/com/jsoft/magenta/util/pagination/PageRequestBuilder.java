package com.jsoft.magenta.util.pagination;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestBuilder {

  public static PageRequest buildPageRequest(int pageIndex, int pageSize, String sortBy,
      boolean asc) {
    Sort sort = Sort.by(sortBy);
    return buildPageRequest(pageIndex, pageSize, sort, asc);
  }

  public static PageRequest buildPageRequest(int pageIndex, int pageSize, String[] sortBy,
      boolean asc) {
    Sort sort = Sort.by(sortBy);
    return buildPageRequest(pageIndex, pageSize, sort, asc);
  }

  public static PageRequest buildPageRequest(int pageIndex, int pageSize, List<String> sortBy,
      boolean asc) {
    String[] sorts = sortBy.toArray(new String[sortBy.size()]);
    return buildPageRequest(pageIndex, pageSize, sorts, asc);
  }

  private static PageRequest buildPageRequest(int pageIndex, int pageSize, Sort sort, boolean asc) {
    sort = asc ? sort.ascending() : sort.descending();
    return PageRequest.of(pageIndex, pageSize, sort);
  }
}
