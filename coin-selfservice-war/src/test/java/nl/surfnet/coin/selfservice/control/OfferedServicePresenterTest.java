package nl.surfnet.coin.selfservice.control;

import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.csa.model.OfferedService;
import nl.surfnet.coin.csa.model.Service;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class OfferedServicePresenterTest {
  private OfferedServicePresenter.OfferedServiceView view;
  private Service service;
  private ArrayList<InstitutionIdentityProvider> identityProviders;
  private OfferedService offeredService;

  @Before
  public void setUp() throws Exception {
    service = new Service(1l, "name", "logo", "website", false, "", "spEntityId");
    identityProviders = new ArrayList<InstitutionIdentityProvider>();
    offeredService = new OfferedService(service, identityProviders);
    view = new OfferedServicePresenter.OfferedServiceView(offeredService);
  }

  @Test
  public void testDisplaysNoIdpNamesIfNone() throws Exception {
    assertEquals("", view.getSortedIdps());
  }

  @Test
  public void testDisplaysSingleIdpName() throws Exception {
    identityProviders.add(new InstitutionIdentityProvider("id", "name", "institutionId"));
    assertEquals("name", view.getSortedIdps());
  }

  @Test
  public void testDisplaysMultipleIdpNamesSortedAlphabatically() throws Exception {
    identityProviders.add(new InstitutionIdentityProvider("id", "name2", "institutionId"));
    identityProviders.add(new InstitutionIdentityProvider("id", "name1", "institutionId"));
    assertEquals("name1,name2", view.getSortedIdps());
  }
}