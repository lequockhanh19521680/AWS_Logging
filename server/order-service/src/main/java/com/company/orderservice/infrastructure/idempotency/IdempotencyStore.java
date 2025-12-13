package com.company.orderservice.infrastructure.idempotency;

import java.util.concurrent.ConcurrentHashMap;

public class IdempotencyStore {
  private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();
  public boolean exists(String key) { return store.containsKey(key); }
  public void put(String key, String value) { store.put(key, value); }
  public String get(String key) { return store.get(key); }
}
