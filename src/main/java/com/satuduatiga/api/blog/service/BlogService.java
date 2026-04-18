package com.satuduatiga.api.blog.service;

import java.util.List;

import com.satuduatiga.api.blog.dto.BlogRequest;
import com.satuduatiga.api.blog.dto.BlogResponse;

public interface BlogService {

    List<BlogResponse> getAllBlog();

    List<BlogResponse> getAllBlogByTag(String tag);

    BlogResponse getBlogById(Long blogId);

    BlogResponse createBlog(BlogRequest blogRequest);

    BlogResponse updateBlog(Long blogId, BlogRequest blogRequest);

    void deleteBlog(Long blogId);

}
