package nl.surfnet.coin.selfservice.service;

import java.util.List;

import com.google.common.base.Optional;

public interface EdugainService {

  List<EdugainApp> getApps();

  Optional<EdugainApp> getApp(Long id);
}
