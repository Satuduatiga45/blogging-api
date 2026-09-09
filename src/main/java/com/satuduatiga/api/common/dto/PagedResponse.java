package com.satuduatiga.api.common.dto;

import java.util.List;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class PagedResponse<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private long totalPages;
    private boolean last;

}
