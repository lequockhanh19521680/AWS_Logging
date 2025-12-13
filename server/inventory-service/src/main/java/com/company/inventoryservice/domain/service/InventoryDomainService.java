package com.company.inventoryservice.domain.service;

public class InventoryDomainService {
  public boolean reserve(int stock, int qty) {
    return stock >= qty;
  }
}
