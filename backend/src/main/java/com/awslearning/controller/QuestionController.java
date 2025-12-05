package com.awslearning.controller;

import com.awslearning.entity.Question;
import com.awslearning.entity.Step;
import com.awslearning.repository.QuestionRepository;
import com.awslearning.repository.StepRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class QuestionController {
  private final QuestionRepository questions;
  private final StepRepository steps;

  public QuestionController(QuestionRepository questions, StepRepository steps) {
    this.questions = questions;
    this.steps = steps;
  }

  @GetMapping("/api/questions")
  public Page<Question> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return questions.findAll(PageRequest.of(page, size));
  }

  @GetMapping("/api/questions/{id}/steps")
  public List<Step> steps(@PathVariable Long id) {
    return steps.findByQuestionIdOrderByStepOrderAsc(id);
  }
}

