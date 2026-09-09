package com.satuduatiga.api.blog.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.satuduatiga.api.blog.dto.BlogRequest;
import com.satuduatiga.api.blog.dto.BlogResponse;
import com.satuduatiga.api.blog.dto.TopicResponse;
import com.satuduatiga.api.blog.entity.BlogEntity;
import com.satuduatiga.api.blog.service.BlogService;
import com.satuduatiga.api.common.dto.PagedResponse;

import static com.satuduatiga.api.blog.mapper.BlogMapper.mapToBlogResponse;
import static com.satuduatiga.api.common.mapper.PageMapper.mapToPagedResponse;

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
    public ResponseEntity<PagedResponse<BlogResponse>> getAllBlogsByTopic(
            @PathVariable String topic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        String formattedTopic = topic;
        if (topic.contains("-")) {
            formattedTopic = topic.replace("-", " ");
        }

        Page<BlogResponse> blogPage = blogService.getAllBlogs(formattedTopic, page, size, sortBy, sortDir);
        return ResponseEntity.ok(mapToPagedResponse(blogPage.getContent(), page, size, blogPage.getTotalElements(),
                blogPage.getTotalPages(), blogPage.isLast()));
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<BlogResponse>> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<BlogResponse> blogPage = blogService.getAllBlogs(null, page, size, sortBy, sortDir);
        return ResponseEntity.ok(mapToPagedResponse(blogPage.getContent(), page, size, blogPage.getTotalElements(),
                blogPage.getTotalPages(), blogPage.isLast()));
    }

    @GetMapping("user/{username}")
    public ResponseEntity<PagedResponse<BlogResponse>> getAllBlogsByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<BlogResponse> blogPage = blogService.getAllBlogsByUser(username, page, size, sortBy, sortDir);

        return ResponseEntity.ok(mapToPagedResponse(blogPage.getContent(), page, size, blogPage.getTotalElements(),
                blogPage.getTotalPages(), blogPage.isLast()));

    }

    @GetMapping("id/{blogId}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable Long blogId) {
        return ResponseEntity.ok(blogService.getBlogById(blogId));
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
