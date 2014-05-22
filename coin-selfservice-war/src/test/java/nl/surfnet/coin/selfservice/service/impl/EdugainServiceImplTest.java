package nl.surfnet.coin.selfservice.service.impl;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import nl.surfnet.coin.selfservice.service.EdugainApp;

public class EdugainServiceImplTest {



  @Test
  public void testRefreshApps() throws Exception {

    URL url = this.getClass().getResource("/edugain/services.xml");

    File file = new File(url.toURI());
    EdugainServiceImpl subject = new EdugainServiceImpl(file);

    assertThat(subject.getApps()).isEmpty();
    subject.refreshApps();
    final List<EdugainApp> edugainApps = subject.getApps();
    assertThat(edugainApps).isNotEmpty();
  }
}