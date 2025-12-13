package com.company.inventoryservice.infrastructure.lock;

import java.util.concurrent.ConcurrentHashMap;

public class LockManager {
  private final ConcurrentHashMap<String, Boolean> locks = new ConcurrentHashMap<>();
  public boolean tryLock(String key) { return locks.putIfAbsent(key, true) == null; }
  public void unlock(String key) { locks.remove(key); }
}
