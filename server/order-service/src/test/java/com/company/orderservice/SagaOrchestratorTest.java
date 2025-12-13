package com.company.orderservice;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.company.orderservice.application.SagaOrchestrator;

public class SagaOrchestratorTest {
  @Test
  void startsSaga() {
    SagaOrchestrator o = new SagaOrchestrator();
    assertTrue(o.start("o1").startsWith("SAGA_STARTED"));
  }
}
