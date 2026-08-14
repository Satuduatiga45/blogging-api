package com.satuduatiga.api.blog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.satuduatiga.api.blog.entity.TopicEntity;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
    Optional<TopicEntity> findByNameIgnoreCase(String name);
}
