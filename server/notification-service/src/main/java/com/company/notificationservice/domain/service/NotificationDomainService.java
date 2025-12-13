package com.company.notificationservice.domain.service;

public class NotificationDomainService {
  public boolean send(String channel, String message) {
    return channel != null && message != null && !message.isEmpty();
  }
}
