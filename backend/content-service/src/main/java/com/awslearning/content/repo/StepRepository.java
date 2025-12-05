package com.awslearning.content.repo;

import com.awslearning.content.entity.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {
  List<Step> findByQuestionIdOrderByStepOrderAsc(Long questionId);
}

