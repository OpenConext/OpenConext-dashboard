package nl.surfnet.coin.selfservice.service.impl;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import nl.surfnet.coin.selfservice.service.DashboardApp;

public class EdugainServiceImplTest {

  @Test
  public void testRefreshApps() throws Exception {
    File file = new File(this.getClass().getResource("/edugain/services.xml").toURI());
    EdugainServiceImpl subject = new EdugainServiceImpl(file);

    assertThat(subject.getApps()).isEmpty();
    subject.refreshApps();
    final List<DashboardApp> edugainApps = subject.getApps();
    assertThat(edugainApps).isNotEmpty();
  }

  @Test
  @Ignore
  public void testRefreshAppsFromWeb() throws Exception {
    EdugainServiceImpl subject = new EdugainServiceImpl(new URI("http://mds.edugain.org/"));
    assertThat(subject.getApps()).isEmpty();
    subject.refreshApps();
    final List<DashboardApp> edugainApps = subject.getApps();
    assertThat(edugainApps).isNotEmpty();
  }

}