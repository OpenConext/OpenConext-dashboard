package nl.surfnet.coin.selfservice.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.scheduling.annotation.Scheduled;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import nl.surfnet.coin.selfservice.service.EdugainApp;
import nl.surfnet.coin.selfservice.service.EdugainService;

public class EdugainServiceImpl implements EdugainService {

  private AtomicReference<List<EdugainApp>> apps = new AtomicReference(new ArrayList<EdugainApp>());

  private Optional<URI> webSource = Optional.absent();
  private Optional<File> fileSource = Optional.absent();

  public EdugainServiceImpl(URI webSource) {
    this.webSource = Optional.of(webSource);
  }

  public EdugainServiceImpl(File source) {
    Preconditions.checkState(source.canRead(), "Can't read the edugain source file: " + source);
    this.fileSource = Optional.of(source);
  }

  @Override
  public List<EdugainApp> getApps() {
    return ImmutableList.copyOf(apps.get());
  }

  @Scheduled(fixedDelay = 1000 * 60 * 60) // refresh after an hour
  public void refreshApps() {
    List<EdugainApp> newApps = new ArrayList<>();

    try(InputStream inputStream = webSource.isPresent()? getContents(webSource.get()) : getContents(fileSource.get())){

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    apps.set(newApps);
  }

  private static InputStream getContents(File file) {
    try {
      return new FileInputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private static InputStream getContents(URI webUri) {
    // use httpclient
    return null;
  }
}
