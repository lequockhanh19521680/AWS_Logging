package com.company.walletservice.domain.service;

import java.math.BigDecimal;

public class WalletDomainService {
  public BigDecimal withdraw(BigDecimal balance, BigDecimal amount) {
    if (balance.compareTo(amount) < 0) throw new IllegalStateException("insufficient");
    return balance.subtract(amount);
  }
}
