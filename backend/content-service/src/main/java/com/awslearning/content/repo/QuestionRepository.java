package com.awslearning.content.repo;

import com.awslearning.content.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepository extends JpaRepository<Question, Long> {
  Page<Question> findByDifficultyLevelIgnoreCase(String difficultyLevel, Pageable pageable);
  Page<Question> findByTagsContainingIgnoreCase(String tag, Pageable pageable);
}
