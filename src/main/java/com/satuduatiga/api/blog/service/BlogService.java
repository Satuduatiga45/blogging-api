package com.satuduatiga.api.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satuduatiga.api.blog.dto.BlogRequest;
import com.satuduatiga.api.blog.dto.BlogResponse;
import com.satuduatiga.api.blog.dto.TopicResponse;
import com.satuduatiga.api.blog.entity.BlogEntity;
import static com.satuduatiga.api.blog.mapper.BlogMapper.mapToBlogResponse;
import static com.satuduatiga.api.blog.mapper.BlogMapper.mapToTopicEntity;
import static com.satuduatiga.api.blog.mapper.BlogMapper.mapToTopicResponse;
import com.satuduatiga.api.blog.repository.BlogRepository;
import com.satuduatiga.api.blog.repository.TopicRepository;
import com.satuduatiga.api.blog.repository.specification.BlogSpecification;
import com.satuduatiga.api.exception.ResourceNotFoundException;
import com.satuduatiga.api.exception.UnauthorizedException;
import com.satuduatiga.api.user.entity.UserEntity;
import com.satuduatiga.api.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final TopicRepository topicRepository;
    private final UserService userService;

    @Transactional
    public List<TopicResponse> getAllTopics() {
        return topicRepository.findAll().stream().map(topic -> mapToTopicResponse(topic)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BlogResponse> getAllBlogs(String topic) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<BlogEntity> blogs;

        if (topic != null && !topic.isBlank()) {
            Specification<BlogEntity> spec = Specification.where(BlogSpecification.hasTopic(topic));
            blogs = blogRepository.findAll(spec, sort);
        } else {
            blogs = blogRepository.findAll(sort);
        }

        return blogs.stream().map(blog -> mapToBlogResponse(blog)).collect(Collectors.toList());
    }

    @Transactional
    public List<BlogResponse> getAllBlogByUser(String username) {
        return blogRepository.findByUserUsername(username).stream().map(blog -> mapToBlogResponse(blog))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BlogResponse getBlogById(Long blogId) {
        BlogEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog could not be found"));
        return mapToBlogResponse(blog);
    }

    @Transactional
    public BlogResponse createBlog(BlogRequest blogRequest) {

        UserEntity user = userService.getCurrentAuthenticatedUser();

        BlogEntity blog = BlogEntity.builder()
                .title(blogRequest.getTitle())
                .content(blogRequest.getContent())
                .user(user)
                .build();

        blog.setTopics(mapToTopicEntity(topicRepository, blogRequest.getTopics()));

        blogRepository.save(blog);

        return mapToBlogResponse(blog);
    }

    @Transactional
    public BlogResponse updateBlog(Long blogId, BlogRequest blogRequest) {

        UserEntity user = userService.getCurrentAuthenticatedUser();
        BlogEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Update failed. Blog could not be found"));

        if (!blog.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to edit this blog");
        }

        blog.setTitle(blogRequest.getTitle());
        blog.setContent(blogRequest.getContent());
        blog.setTopics(mapToTopicEntity(topicRepository, blogRequest.getTopics()));

        BlogEntity updatedBlog = blogRepository.save(blog);
        return mapToBlogResponse(updatedBlog);

    }

    @Transactional
    public void deleteBlog(Long blogId) {
        UserEntity user = userService.getCurrentAuthenticatedUser();
        BlogEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Update failed. Blog could not be found"));

        if (!blog.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to edit this blog");
        }

        blogRepository.delete(blog);
    }

    @Transactional
    public void deleteBlogAsAdmin(Long blogId) {
        BlogEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Delete failed. Blog could not be found"));
        blogRepository.delete(blog);
    }

}
