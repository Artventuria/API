package com.artventuria.api.repository.jpa.artwork;

import com.artventuria.api.domain.postgresql.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Integer> {
    List<Collection> findByUserId(Integer userId);
}