package com.company.paymentservice.domain.service;

import java.math.BigDecimal;

public class PaymentDomainService {
  public boolean authorize(BigDecimal amount, String currency) {
    return amount.compareTo(BigDecimal.ZERO) > 0 && currency != null;
  }
}
