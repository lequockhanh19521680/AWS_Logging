package com.company.walletservice;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.testcontainers.containers.PostgreSQLContainer;

public class LedgerIntegrationTest {
  @Test
  void startsPostgresContainer() {
    try (PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16")) {
      pg.start();
      assertNotNull(pg.getJdbcUrl());
    }
  }
}
