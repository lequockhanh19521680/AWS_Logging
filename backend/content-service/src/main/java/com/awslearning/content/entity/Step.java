package com.awslearning.content.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "steps")
@Data
public class Step {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long questionId;
  private Integer stepOrder;
  private String content;
  private String serviceIcon;
}

