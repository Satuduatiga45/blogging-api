package com.satuduatiga.api.blog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.satuduatiga.api.blog.dto.BlogRequest;
import com.satuduatiga.api.blog.dto.BlogResponse;
import com.satuduatiga.api.blog.dto.TopicResponse;
import com.satuduatiga.api.blog.service.BlogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping("topic")
    public ResponseEntity<List<TopicResponse>> getAllTopics() {
        return ResponseEntity.ok(blogService.getAllTopics());
    }

    @GetMapping("topic/{topic}")
    public ResponseEntity<List<BlogResponse>> getAllBlogsByTopic(@PathVariable String topic) {
        String formattedTopic = topic;
        if (topic.contains("-")) {
            formattedTopic = topic.replace("-", " ");
        }
        return ResponseEntity.ok(blogService.getAllBlogs(formattedTopic));
    }

    @GetMapping()
    public ResponseEntity<List<BlogResponse>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs(null));
    }

    @GetMapping("id/{blogId}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable Long blogId) {
        return ResponseEntity.ok(blogService.getBlogById(blogId));
    }

    @GetMapping("user/{username}")
    public ResponseEntity<List<BlogResponse>> getAllBlogsByUser(@PathVariable String username) {
        return ResponseEntity.ok(blogService.getAllBlogByUser(username));
    }

    @PostMapping("user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BlogResponse> createBlog(@Valid @RequestBody BlogRequest blogRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blogService.createBlog(blogRequest));
    }

    @PatchMapping("user/{blogId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BlogResponse> updateBlog(@PathVariable Long blogId,
            @Valid @RequestBody BlogRequest blogRequest) {
        return ResponseEntity.ok(blogService.updateBlog(blogId, blogRequest));
    }

    @DeleteMapping("user/{blogId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlog(@PathVariable() Long blogId) {
        blogService.deleteBlog(blogId);
    }

    @DeleteMapping("admin/{blogId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlogAsAdmin(@PathVariable Long blogId) {
        blogService.deleteBlogAsAdmin(blogId);
    }

}
