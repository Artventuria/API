package com.artventuria.api.repository.mongo;

import com.artventuria.api.domain.mongodb.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {
    List<Activity> findByUserIdOrderByTimestampDesc(Integer userId);

    List<Activity> findByUserIdAndTimestampBetweenOrderByTimestampDesc(
            Integer userId,
            Instant startTime,
            Instant endTime);

    List<Activity> findByTypeOrderByTimestampDesc(String type);

    @Query(value = "{userId: ?0}", delete = true)
    void deleteByUserId(Integer userId);
}