package nl.surfnet.coin.selfservice.service;

public interface EmailService {
  void sendMail(String issueKey, String from, String subject, String body);
}
