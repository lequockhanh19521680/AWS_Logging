package com.company.loyaltyservice.domain.service;

public class LoyaltyDomainService {
  public long accrue(long basePoints, double multiplier) {
    return Math.round(basePoints * multiplier);
  }
}
