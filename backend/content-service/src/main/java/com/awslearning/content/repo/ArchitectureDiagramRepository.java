package com.awslearning.content.repo;

import com.awslearning.content.entity.ArchitectureDiagram;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ArchitectureDiagramRepository extends JpaRepository<ArchitectureDiagram, Long> {
  Optional<ArchitectureDiagram> findByQuestionId(Long questionId);
}

