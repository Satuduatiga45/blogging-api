package com.satuduatiga.api.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satuduatiga.api.blog.dto.BlogRequest;
import com.satuduatiga.api.blog.dto.BlogResponse;
import com.satuduatiga.api.blog.entity.BlogEntity;
import static com.satuduatiga.api.blog.mapper.BlogMapper.mapToBlogResponse;
import com.satuduatiga.api.blog.repository.BlogRepository;
import com.satuduatiga.api.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    @Transactional(readOnly = true)
    public List<BlogResponse> getAllBlogs() {
        List<BlogEntity> blogs = blogRepository.findAllByOrderByIdAsc();
        return blogs.stream().map(blog -> mapToBlogResponse(blog)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BlogResponse getBlogById(Long blogId) {
        BlogEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog could not be found"));
        return mapToBlogResponse(blog);
    }

    @Transactional
    public BlogResponse createBlog(BlogRequest blogRequest) {
        BlogEntity blog = BlogEntity.builder()
                .title(blogRequest.getTitle())
                .content(blogRequest.getContent())
                .category(blogRequest.getCategory())
                .tags(blogRequest.getTags())
                .build();
        BlogEntity newBlog = blogRepository.save(blog);
        return mapToBlogResponse(newBlog);
    }

    @Transactional
    public BlogResponse updateBlog(Long blogId, BlogRequest blogRequest) {
        BlogEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Update failed. Blog could not be found"));
        blog.setTitle(blogRequest.getTitle());
        blog.setContent(blogRequest.getContent());
        blog.setCategory(blogRequest.getCategory());
        blog.setTags(blogRequest.getTags());

        BlogEntity updatedBlog = blogRepository.save(blog);
        return mapToBlogResponse(updatedBlog);
    }

    @Transactional
    public void deleteBlog(Long blogId) {
        BlogEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Delete failed. Blog could not be found"));
        blogRepository.delete(blog);
    }

    @Transactional
    public List<BlogResponse> getAllBlogsByTag(String tag) {
        List<BlogEntity> blogs = blogRepository.findByTagsContaining(tag);
        return blogs.stream().map(blog -> mapToBlogResponse(blog)).collect(Collectors.toList());
    }

}
