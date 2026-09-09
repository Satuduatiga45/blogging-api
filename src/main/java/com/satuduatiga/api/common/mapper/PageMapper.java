package com.satuduatiga.api.common.mapper;

import java.util.List;

import com.satuduatiga.api.common.dto.PagedResponse;

public class PageMapper {
    public static <T> PagedResponse<T> mapToPagedResponse(List<T> content, int pageNumber, int size, long totalElements,
            long totalPages, boolean isLast) {
        return PagedResponse.<T>builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .last(isLast)
                .build();
    }
}
