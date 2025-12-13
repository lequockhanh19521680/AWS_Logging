package com.company.orderservice.application;

public class SagaOrchestrator {
  public String start(String orderId) {
    return "SAGA_STARTED:" + orderId;
  }
  public String onInventoryReserved(String orderId) {
    return "INVENTORY_RESERVED:" + orderId;
  }
  public String onPaymentAuthorized(String orderId) {
    return "PAYMENT_AUTHORIZED:" + orderId;
  }
  public String onLoyaltyAccrued(String orderId) {
    return "LOYALTY_ACCRUED:" + orderId;
  }
  public String onFailure(String orderId, String reason) {
    return "CANCELLED:" + orderId + ":" + reason;
  }
}
