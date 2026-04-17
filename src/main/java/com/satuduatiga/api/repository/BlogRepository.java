package com.satuduatiga.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.satuduatiga.api.entity.Blog;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    @Override
    @EntityGraph(attributePaths = { "tags" })
    Optional<Blog> findById(Long id);

    @EntityGraph(attributePaths = { "tags" })
    List<Blog> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = { "tags" })
    List<Blog> findByTagsContaining(String tag);
}
