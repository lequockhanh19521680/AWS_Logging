package com.awslearning.saga.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
public class SagaController {
  private final RestTemplate rt = new RestTemplate();

  @PostMapping("/api/saga/create-scenario")
  public ResponseEntity<?> createScenario(@RequestBody Map<String, Object> payload) {
    try {
      Map<String, Object> question = (Map<String, Object>) payload.get("question");
      String imagePath = (String) payload.get("imagePath");
      // 1) Create question
      var qRes = rt.postForEntity("http://localhost:8082/api/questions", question, Map.class);
      Long questionId = Long.valueOf(String.valueOf(((Map<?,?>)qRes.getBody()).get("id")));
      // 2) Link diagram (simulate)
      var diagram = Map.of("questionId", questionId, "imageUrl", imagePath, "diagramJson", "{}");
      rt.postForEntity("http://localhost:8082/api/diagrams", diagram, Map.class);
      return ResponseEntity.ok(Map.of("status", "ok", "questionId", questionId));
    } catch (Exception e) {
      // Compensation: attempt delete question
      try {
        String qid = String.valueOf(((Map<?,?>)payload.get("question")).get("id"));
        rt.delete("http://localhost:8082/api/questions/" + qid);
      } catch (Exception ignored) {}
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }
}

