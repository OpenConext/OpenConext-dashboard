package selfservice.service;

public interface EmailService {

  void sendMail(String from, String subject, String body);

}
