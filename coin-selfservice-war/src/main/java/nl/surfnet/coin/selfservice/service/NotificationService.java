package nl.surfnet.coin.selfservice.service;

public interface NotificationService {
  void sendMail(String issueKey, String from, String subject, String body);
}
