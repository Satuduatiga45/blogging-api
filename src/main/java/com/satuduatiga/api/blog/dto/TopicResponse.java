package com.satuduatiga.api.blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicResponse {
    private Long id;
    private String name;
}
