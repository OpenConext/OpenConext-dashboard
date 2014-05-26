package nl.surfnet.coin.selfservice.service;

import java.util.List;

import com.google.common.base.Optional;

public interface EdugainService {

  List<DashboardApp> getApps();

  Optional<DashboardApp> getApp(Long id);
}
