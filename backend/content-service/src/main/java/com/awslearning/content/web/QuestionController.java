package com.awslearning.content.web;

import com.awslearning.content.entity.Question;
import com.awslearning.content.entity.Step;
import com.awslearning.content.repo.QuestionRepository;
import com.awslearning.content.repo.StepRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuestionController {
  private final QuestionRepository questions;
  private final StepRepository steps;
  private final com.awslearning.content.repo.ArchitectureDiagramRepository diagrams;
  public QuestionController(QuestionRepository questions, StepRepository steps, com.awslearning.content.repo.ArchitectureDiagramRepository diagrams) { this.questions = questions; this.steps = steps; this.diagrams = diagrams; }

  @GetMapping("/questions")
  public Page<Question> list(@RequestParam(defaultValue="0") int page,
                             @RequestParam(defaultValue="10") int size,
                             @RequestParam(required=false) String difficulty,
                             @RequestParam(required=false) String tag) {
    var pageable = PageRequest.of(page, size);
    if (difficulty != null && tag != null) {
      // simple intersection by fetching tag subset and filtering difficulty in-memory
      var tagPage = questions.findByTagsContainingIgnoreCase(tag, pageable);
      var filtered = tagPage.getContent().stream()
          .filter(q -> difficulty.equalsIgnoreCase(q.getDifficultyLevel())).toList();
      return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
    }
    if (difficulty != null) return questions.findByDifficultyLevelIgnoreCase(difficulty, pageable);
    if (tag != null) return questions.findByTagsContainingIgnoreCase(tag, pageable);
    return questions.findAll(pageable);
  }
  @GetMapping("/questions/{id}/steps")
  public List<Step> steps(@PathVariable Long id) { return steps.findByQuestionIdOrderByStepOrderAsc(id); }

  @PostMapping("/questions")
  public Question createQuestion(@RequestBody Question q) {
    return questions.save(q);
  }

  @DeleteMapping("/questions/{id}")
  public Map<String, Object> deleteQuestion(@PathVariable Long id) {
    questions.deleteById(id);
    return Map.of("deleted", true);
  }

  @PostMapping("/diagrams")
  public com.awslearning.content.entity.ArchitectureDiagram createDiagram(@RequestBody com.awslearning.content.entity.ArchitectureDiagram d) {
    return diagrams.save(d);
  }
}
