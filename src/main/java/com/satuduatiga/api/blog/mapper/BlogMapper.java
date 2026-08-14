package com.satuduatiga.api.blog.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.satuduatiga.api.blog.dto.BlogResponse;
import com.satuduatiga.api.blog.dto.TopicResponse;
import com.satuduatiga.api.blog.entity.BlogEntity;
import com.satuduatiga.api.blog.entity.TopicEntity;
import com.satuduatiga.api.blog.repository.TopicRepository;

public class BlogMapper {
    public static BlogResponse mapToBlogResponse(BlogEntity blog) {
        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .topics(blog.getTopics().stream().map(topic -> topic.getName()).collect(Collectors.toSet()))
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }

    public static TopicResponse mapToTopicResponse(TopicEntity topic) {
        return TopicResponse.builder()
                .id(topic.getId())
                .name(topic.getName())
                .build();
    }

    public static Set<TopicEntity> mapToTopicEntity(TopicRepository topicRepository, Set<String> requestedTopics) {
        Set<TopicEntity> topics = requestedTopics.stream().map(
                topic -> {
                    String cleanName = topic.trim().toLowerCase();

                    return topicRepository.findByNameIgnoreCase(cleanName).orElseGet(() -> {
                        TopicEntity newTopic = new TopicEntity();
                        newTopic.setName(cleanName);
                        return topicRepository.save(newTopic);
                    });
                }).collect(Collectors.toSet());

        return topics;
    }

}
