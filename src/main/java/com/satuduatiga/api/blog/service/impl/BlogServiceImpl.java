package com.satuduatiga.api.blog.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satuduatiga.api.blog.dto.BlogRequest;
import com.satuduatiga.api.blog.dto.BlogResponse;
import com.satuduatiga.api.blog.entity.BlogEntity;
import static com.satuduatiga.api.blog.mapper.BlogMapper.mapToBlogResponse;
import com.satuduatiga.api.blog.repository.BlogRepository;
import com.satuduatiga.api.blog.service.BlogService;
import com.satuduatiga.api.exception.BlogNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BlogResponse> getAllBlog() {
        List<BlogEntity> blogs = blogRepository.findAllByOrderByIdAsc();
        return blogs.stream().map(blog -> mapToBlogResponse(blog)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BlogResponse getBlogById(Long blogId) {
        BlogEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog could not be found"));
        return mapToBlogResponse(blog);
    }

    @Override
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

    @Override
    @Transactional
    public BlogResponse updateBlog(Long blogId, BlogRequest blogRequest) {
        BlogEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Update failed. Blog could not be found"));
        blog.setTitle(blogRequest.getTitle());
        blog.setContent(blogRequest.getContent());
        blog.setCategory(blogRequest.getCategory());
        blog.setTags(blogRequest.getTags());

        BlogEntity updatedBlog = blogRepository.save(blog);
        return mapToBlogResponse(updatedBlog);
    }

    @Override
    @Transactional
    public void deleteBlog(Long blogId) {
        BlogEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Delete failed. Blog could not be found"));
        blogRepository.delete(blog);
    }

    @Override
    public List<BlogResponse> getAllBlogByTag(String tag) {
        List<BlogEntity> blogs = blogRepository.findByTagsContaining(tag);
        return blogs.stream().map(blog -> mapToBlogResponse(blog)).collect(Collectors.toList());
    }

}
