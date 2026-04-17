package com.satuduatiga.api.mapper;

import com.satuduatiga.api.dto.BlogResponse;
import com.satuduatiga.api.entity.Blog;

public class BlogMapper {
    public static BlogResponse mapToBlogResponse(Blog blog) {
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
