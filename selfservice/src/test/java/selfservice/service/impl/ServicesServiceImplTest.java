package selfservice.service.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static selfservice.domain.csa.Field.Source.DISTRIBUTIONCHANNEL;
import static selfservice.domain.csa.Field.Source.LMNG;
import static selfservice.domain.csa.Field.Source.SURFCONEXT;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import selfservice.domain.Service;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.domain.csa.ContactPerson;
import selfservice.domain.csa.ContactPersonType;
import selfservice.domain.csa.Field.Key;
import selfservice.domain.csa.FieldString;
import selfservice.service.CrmService;

public class ServicesServiceImplTest {

  private ServicesServiceImpl subject;

  @Mock
  private CompoundServiceProviderService compoundServiceProviderServiceMock;

  @Mock
  private CrmService crmServiceMock;

  @Before
  public void setup() {
     subject = new ServicesServiceImpl(compoundServiceProviderServiceMock, crmServiceMock, "http://example.com", "http://deeplink.com", new String[] {});
  }

  @Test
  public void emailAddressesShouldBeNormalized() {
    ServiceProvider serviceProvider = new ServiceProvider(ImmutableMap.of("entityid", "sp-id"));
    serviceProvider.addContactPerson(new ContactPerson("John Doe", "mailto:john@example.com", "1234", ContactPersonType.help));
    CompoundServiceProvider csp = new CompoundServiceProvider();
    csp.setId(1L);
    csp.setServiceProvider(serviceProvider);
    csp.setFields(ImmutableSortedSet.of(
        new FieldString(DISTRIBUTIONCHANNEL, Key.WIKI_URL_EN, "http://wiki.example.com/en"),
        new FieldString(DISTRIBUTIONCHANNEL, Key.WIKI_URL_NL, "http://wiki.example.com/nl"),
        new FieldString(LMNG, Key.INSTITUTION_DESCRIPTION_EN, "Institution en"),
        new FieldString(LMNG, Key.INSTITUTION_DESCRIPTION_NL, "Institution nl"),
        new FieldString(SURFCONEXT, Key.SUPPORT_URL_EN, "http://support.example.com/en"),
        new FieldString(SURFCONEXT, Key.SUPPORT_URL_NL, "http://support.example.com/nl"),
        new FieldString(SURFCONEXT, Key.TITLE_EN, "Title en"),
        new FieldString(SURFCONEXT, Key.TITLE_NL, "Title nl"),
        new FieldString(LMNG, Key.ENDUSER_DESCRIPTION_EN, "End user description en"),
        new FieldString(LMNG, Key.ENDUSER_DESCRIPTION_NL, "End user description nl"),
        new FieldString(SURFCONEXT, Key.SERVICE_DESCRIPTION_EN, "Service Description en"),
        new FieldString(SURFCONEXT, Key.SERVICE_DESCRIPTION_NL, "Service Description nl"),
        new FieldString(SURFCONEXT, Key.SERVICE_URL, "http://service.example.com"),
        new FieldString(SURFCONEXT, Key.APPSTORE_LOGO, "logo2.img"),
        new FieldString(SURFCONEXT, Key.DETAIL_LOGO, "logo.img"),
        new FieldString(SURFCONEXT, Key.EULA_URL, "http://service.example.com/eula"),
        new FieldString(SURFCONEXT, Key.APP_URL, "http://service.example.com"),
        new FieldString(SURFCONEXT, Key.SUPPORT_MAIL, "mailto:notused@example.com")
    ));

    Service service = subject.buildApiService(csp, "nl");

    assertThat(service.getSupportMail(), is("john@example.com"));
  }
}
