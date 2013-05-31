package nl.surfnet.coin.selfservice.service;

public interface Cruncher {
  String getLogins();
  String getLoginsByIdpAndSp(final String idpEntityId, final String spEntityId);
  String getLoginsByIdp(final String idpEntityId);
  String getLoginsBySp(final String spEntityId);
}
