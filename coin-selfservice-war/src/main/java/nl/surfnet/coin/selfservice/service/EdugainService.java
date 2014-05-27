package nl.surfnet.coin.selfservice.service;

import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;

public interface EdugainService {

  List<DashboardApp> getApps(Set<String> spEntityIdsToFilterOut);

  Optional<DashboardApp> getApp(Long id);
}
