package com.satuduatiga.api.blog.mapper;

import com.satuduatiga.api.blog.dto.BlogResponse;
import com.satuduatiga.api.blog.entity.BlogEntity;

public class BlogMapper {
    public static BlogResponse mapToBlogResponse(BlogEntity blog) {
        BlogResponse blogResponse = BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .category(blog.getCategory())
                .tags(blog.getTags())
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
        return blogResponse;
    }
}
