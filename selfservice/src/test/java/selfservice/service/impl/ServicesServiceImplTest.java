package selfservice.service.impl;

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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static selfservice.domain.csa.Field.Source.DISTRIBUTIONCHANNEL;
import static selfservice.domain.csa.Field.Source.SURFCONEXT;

public class ServicesServiceImplTest {

  private ServicesServiceImpl subject;

  @Mock
  private CompoundServiceProviderService compoundServiceProviderServiceMock;

  @Before
  public void setup() {
     subject = new ServicesServiceImpl(compoundServiceProviderServiceMock, "http://example.com");
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
        new FieldString(DISTRIBUTIONCHANNEL, Key.INSTITUTION_DESCRIPTION_EN, "Institution en"),
        new FieldString(DISTRIBUTIONCHANNEL, Key.INSTITUTION_DESCRIPTION_NL, "Institution nl"),
        new FieldString(SURFCONEXT, Key.SUPPORT_URL_EN, "http://support.example.com/en"),
        new FieldString(SURFCONEXT, Key.SUPPORT_URL_NL, "http://support.example.com/nl"),
        new FieldString(SURFCONEXT, Key.TITLE_EN, "Title en"),
        new FieldString(SURFCONEXT, Key.TITLE_NL, "Title nl"),
        new FieldString(SURFCONEXT, Key.SERVICE_DESCRIPTION_EN, "Service Description en"),
        new FieldString(SURFCONEXT, Key.SERVICE_DESCRIPTION_NL, "Service Description nl"),
        new FieldString(SURFCONEXT, Key.SERVICE_URL, "http://service.example.com"),
        new FieldString(SURFCONEXT, Key.APPSTORE_LOGO, "logo2.img"),
        new FieldString(SURFCONEXT, Key.DETAIL_LOGO, "logo.img"),
        new FieldString(SURFCONEXT, Key.EULA_URL, "http://service.example.com/eula"),
        new FieldString(SURFCONEXT, Key.APP_URL, "http://service.example.com"),
        new FieldString(SURFCONEXT, Key.SUPPORT_MAIL, "mailto:notused@example.com"),
        new FieldString(SURFCONEXT, Key.INTERFED_SOURCE, "eduGAIN"),
        new FieldString(SURFCONEXT, Key.PRIVACY_STATEMENT_URL_EN, "http://privacy.statement/en"),
        new FieldString(SURFCONEXT, Key.PRIVACY_STATEMENT_URL_NL, "http://privacy.statement/nl"),
        new FieldString(SURFCONEXT, Key.REGISTRATION_INFO_URL, "http://registration.info"),
        new FieldString(SURFCONEXT, Key.REGISTRATION_POLICY_URL_EN, "http://registration.policy/en"),
        new FieldString(SURFCONEXT, Key.REGISTRATION_POLICY_URL_NL, "http://registration.policy/nl"),
        new FieldString(SURFCONEXT, Key.ENTITY_CATEGORIES_1, "http://entity.cat/1"),
        new FieldString(SURFCONEXT, Key.ENTITY_CATEGORIES_2, "http://entity.cat/2"),
        new FieldString(SURFCONEXT, Key.PUBLISH_IN_EDUGAIN_DATE, "2016-10-15T15:10:11Z")
    ));

    Service service = subject.buildApiService(csp, "nl");

    assertThat(service.getSupportMail(), is("john@example.com"));
  }
}
