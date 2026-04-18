package com.satuduatiga.api.blog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.satuduatiga.api.blog.service.BlogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping("posts")
    public ResponseEntity<List<BlogResponse>> getAllBlog(@RequestParam(required = false) String tag) {
        if (tag != null && !tag.isEmpty()) {
            return ResponseEntity.ok(blogService.getAllBlogByTag(tag));
        }
        return ResponseEntity.ok(blogService.getAllBlog());
    }

    @GetMapping("posts/{blogId}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable Long blogId) {
        return ResponseEntity.ok(blogService.getBlogById(blogId));
    }

    @PostMapping("posts")
    public ResponseEntity<BlogResponse> createNewBlog(@Valid @RequestBody BlogRequest blogRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blogService.createBlog(blogRequest));
    }

    @PatchMapping("posts/{blogId}")
    public ResponseEntity<BlogResponse> updateBlog(@PathVariable Long blogId,
            @Valid @RequestBody BlogRequest blogRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(blogService.updateBlog(blogId, blogRequest));
    }

    @DeleteMapping("posts/{blogId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlog(@PathVariable("blogId") Long blogId) {
        blogService.deleteBlog(blogId);
    }

}
