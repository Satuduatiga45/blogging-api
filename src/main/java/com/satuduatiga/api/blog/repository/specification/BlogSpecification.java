package com.satuduatiga.api.blog.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.satuduatiga.api.blog.entity.BlogEntity;
import com.satuduatiga.api.blog.entity.TopicEntity;

import jakarta.persistence.criteria.Join;

public class BlogSpecification {

    public static Specification<BlogEntity> hasTopic(String topic) {
        return (root, query, cb) -> {
            if (topic == null || topic.isBlank()) {
                return null;
            }

            query.distinct(true);

            Join<BlogEntity, TopicEntity> topicJoin = root.join("topics");

            return cb.equal(topicJoin.get("name"), topic);
        };
    }
}
