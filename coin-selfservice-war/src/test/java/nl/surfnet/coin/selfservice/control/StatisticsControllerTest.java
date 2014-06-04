package nl.surfnet.coin.selfservice.control;

import nl.surfnet.coin.selfservice.util.Helpers;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.surfnet.cruncher.Cruncher;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsControllerTest {
  private static final String IDP_ENTITY_ID = "id";
  private static final String SP_ENTITY_ID = "spEntityId";
  @InjectMocks
  private StatisticsController controller = new StatisticsController();

  @Mock
  private Cruncher cruncher;
  private MockHttpServletRequest request;
  private MockHttpServletResponse mockHttpServletResponse;
  private LocalDate startDate;
  private LocalDate endDateOfMonth;
  private String json;

  @Before
  public void setUp() throws Exception {
    request = Helpers.defaultMockRequest();
    mockHttpServletResponse = new MockHttpServletResponse();
    startDate = new LocalDate(2013, 1, 1);
    endDateOfMonth = new LocalDate(2013, 1, 31);
    json = IOUtils.toString(getClass().getResourceAsStream("/stat-json/stats-idp-sp.json"));
  }

  @Test
  public void testDownloadCsvWithOnlyIdp() throws Exception {
    when(cruncher.getLoginsByIdp(startDate.toDate(), endDateOfMonth.toDate(), IDP_ENTITY_ID)).thenReturn(json);

    controller.csvStats(startDate.toDateTimeAtStartOfDay().getMillis(), "month", null, request, mockHttpServletResponse);

    verify(cruncher, times(1)).getLoginsByIdp(startDate.toDate(), endDateOfMonth.toDate(), IDP_ENTITY_ID);
    assertNotNull(mockHttpServletResponse.getContentAsString());
  }

  @Test
  public void testDownloadCsvWithIdpAndSp() throws Exception {
    when(cruncher.getLoginsByIdpAndSp(startDate.toDate(), endDateOfMonth.toDate(), IDP_ENTITY_ID, SP_ENTITY_ID)).thenReturn(json);

    controller.csvStats(startDate.toDateTimeAtStartOfDay().getMillis(), "month", SP_ENTITY_ID, request, mockHttpServletResponse);

    verify(cruncher).getLoginsByIdpAndSp(startDate.toDate(), endDateOfMonth.toDate(), IDP_ENTITY_ID, SP_ENTITY_ID);
    assertNotNull(mockHttpServletResponse.getContentAsString());
  }

  @Ignore("javascript to calculate weeknumbers is broken. first fix that.")
  public void testDownloadCsvByWeek() throws Exception {
    when(cruncher.getLoginsByIdp(startDate.toDate(), new LocalDate(2013, 1, 5).toDate(), IDP_ENTITY_ID)).thenReturn(json);
    controller.csvStats(startDate.toDateTimeAtStartOfDay().getMillis(), "week", null, request, mockHttpServletResponse);

    verify(cruncher).getLoginsByIdp(startDate.toDate(), new LocalDate(2013, 1, 5).toDate(), IDP_ENTITY_ID);
    assertNotNull(mockHttpServletResponse.getContentAsString());
  }

}