package nl.surfnet.coin.selfservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Condition;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.service.DashboardApp;

public class EdugainServiceImplTest {

  private static final String preExistingEntityId = "https://applications.eumedgrid.eu/shibboleth";

  private final Condition<Service> preExisting = new Condition<Service>("pre existing in the set of supplied SP Entity Id's") {
    @Override
    public boolean matches(Service value) {
      return preExistingEntityId.equals(value.getSpEntityId());
    }
  };

  @Test
  public void testRefreshApps() throws Exception {
    File file = new File(this.getClass().getResource("/edugain/services.xml").toURI());
    EdugainServiceImpl subject = new EdugainServiceImpl(file);

    assertThat(subject.getApps(Collections.<String>emptySet())).isEmpty();
    subject.refreshApps();

    final List<DashboardApp> edugainApps = subject.getApps(ImmutableSet.of(preExistingEntityId));
    assertThat(edugainApps).isNotEmpty();
    assertThat(edugainApps).areNot(preExisting);
  }

  @Test
  @Ignore
  public void testRefreshAppsFromWeb() throws Exception {
    EdugainServiceImpl subject = new EdugainServiceImpl(new URI("http://mds.edugain.org/"));
    subject.refreshApps();
    final List<DashboardApp> edugainApps = subject.getApps(Collections.<String>emptySet());
    assertThat(edugainApps).isNotEmpty();
  }

}