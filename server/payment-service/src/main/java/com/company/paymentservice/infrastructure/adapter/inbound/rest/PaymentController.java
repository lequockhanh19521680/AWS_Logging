package com.company.paymentservice.infrastructure.adapter.inbound.rest;

import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.company.paymentservice.domain.service.PaymentDomainService;

@RestController
public class PaymentController {
  private final PaymentDomainService service = new PaymentDomainService();

  @PostMapping("/payments/process")
  public ResponseEntity<String> process(@RequestParam BigDecimal amount, @RequestParam String currency) {
    boolean ok = service.authorize(amount, currency);
    return ok ? ResponseEntity.ok("AUTHORIZED") : ResponseEntity.badRequest().body("REJECTED");
  }
}
