package com.satuduatiga.api.service.impl;

import static com.satuduatiga.api.mapper.BlogMapper.mapToBlogResponse;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satuduatiga.api.dto.BlogRequest;
import com.satuduatiga.api.dto.BlogResponse;
import com.satuduatiga.api.entity.Blog;
import com.satuduatiga.api.exception.BlogNotFoundException;
import com.satuduatiga.api.repository.BlogRepository;
import com.satuduatiga.api.service.BlogService;

@Service
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    @Autowired
    public BlogServiceImpl(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogResponse> getAllBlog() {
        List<Blog> blogs = blogRepository.findAllByOrderByIdAsc();
        return blogs.stream().map(blog -> mapToBlogResponse(blog)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BlogResponse getBlogById(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog could not be found"));
        return mapToBlogResponse(blog);
    }

    @Override
    @Transactional
    public BlogResponse createBlog(BlogRequest blogRequest) {
        Blog blog = Blog.builder()
                .title(blogRequest.getTitle())
                .content(blogRequest.getContent())
                .category(blogRequest.getCategory())
                .tags(blogRequest.getTags())
                .build();
        Blog newBlog = blogRepository.save(blog);
        return mapToBlogResponse(newBlog);
    }

    @Override
    @Transactional
    public BlogResponse updateBlog(Long blogId, BlogRequest blogRequest) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Update failed. Blog could not be found"));
        blog.setTitle(blogRequest.getTitle());
        blog.setContent(blogRequest.getContent());
        blog.setCategory(blogRequest.getCategory());
        blog.setTags(blogRequest.getTags());

        Blog updatedBlog = blogRepository.save(blog);
        return mapToBlogResponse(updatedBlog);
    }

    @Override
    @Transactional
    public void deleteBlog(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Delete failed. Blog could not be found"));
        blogRepository.delete(blog);
    }

    @Override
    public List<BlogResponse> getAllBlogByTag(String tag) {
        List<Blog> blogs = blogRepository.findByTagsContaining(tag);
        return blogs.stream().map(blog -> mapToBlogResponse(blog)).collect(Collectors.toList());
    }

}
