package com.awslearning.content.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "architecture_diagrams")
@Data
public class ArchitectureDiagram {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long questionId;
  private String imageUrl;
  @Column(columnDefinition = "TEXT")
  private String diagramJson;
}

