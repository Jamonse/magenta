package com.jsoft.magenta.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestBuilder
{
    public static PageRequest buildPageRequest(int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        Sort sort = Sort.by(sortBy);
        sort = asc ? sort.ascending() : sort.descending();
        return PageRequest.of(pageIndex, pageSize, sort);
    }
}
