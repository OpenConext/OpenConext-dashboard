package nl.surfnet.coin.selfservice.service;

import nl.surfnet.coin.csa.model.Service;

/**
 * The UI knows how to deal with 'Service' objects, so we'll just extend that
 */
public class DashboardApp extends Service {

  private boolean edugain = false;

  public boolean isEdugain() {
    return edugain;
  }

  public void setEdugain(boolean edugain) {
    this.edugain = edugain;
  }
}
