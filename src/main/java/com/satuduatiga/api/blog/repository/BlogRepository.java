package com.satuduatiga.api.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.satuduatiga.api.blog.entity.BlogEntity;

public interface BlogRepository extends JpaRepository<BlogEntity, Long> {
    @Override
    @EntityGraph(attributePaths = { "tags" })
    Optional<BlogEntity> findById(Long id);

    @EntityGraph(attributePaths = { "tags" })
    List<BlogEntity> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = { "tags" })
    List<BlogEntity> findByTagsContaining(String tag);
}
