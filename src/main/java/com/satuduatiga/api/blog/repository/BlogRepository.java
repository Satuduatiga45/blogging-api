package com.satuduatiga.api.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.satuduatiga.api.blog.entity.BlogEntity;

public interface BlogRepository extends JpaRepository<BlogEntity, Long>, JpaSpecificationExecutor<BlogEntity> {
    @Override
    @EntityGraph(attributePaths = "topics")
    Optional<BlogEntity> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "topics")
    List<BlogEntity> findAll(Specification<BlogEntity> spec);

    @Override
    @EntityGraph(attributePaths = "topics")
    List<BlogEntity> findAll();

    @EntityGraph(attributePaths = "topics")
    List<BlogEntity> findByUserUsername(String username);

}
