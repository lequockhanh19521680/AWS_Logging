package com.awslearning.content.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "questions")
@Data
public class Question {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  private String difficultyLevel;
  private String scenarioDescription;
  private String thumbnailUrl;
  @Column(columnDefinition = "TEXT")
  private String tags;
}
