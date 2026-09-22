package com.lomeu.votingapi.service;

import com.lomeu.votingapi.dto.request.CreateTopicRequest;
import com.lomeu.votingapi.entity.Topic;
import com.lomeu.votingapi.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class TopicService {

    private static final Logger log = LoggerFactory.getLogger(TopicService.class);

    private final TopicRepository topicRepository;
    private final Clock clock;

    public TopicService(TopicRepository topicRepository, Clock clock) {
        this.topicRepository = topicRepository;
        this.clock = clock;
    }

    @Transactional
    public Topic createTopic(CreateTopicRequest request) {
        Topic topic = topicRepository.save(new Topic(request.title(), clock.instant()));
        log.info("Topic created with id {}", topic.getId());
        return topic;
    }
}
