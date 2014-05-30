package nl.surfnet.coin.selfservice.control;

import nl.surfnet.coin.selfservice.util.Helpers;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.surfnet.cruncher.Cruncher;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsControllerTest {
  @InjectMocks
  private StatisticsController controller = new StatisticsController();

  @Mock
  private Cruncher cruncher;
  private MockHttpServletRequest request;
  private MockHttpServletResponse mockHttpServletResponse;
  private LocalDate startDate;
  private LocalDate endDate;
  private String json;

  @Before
  public void setUp() throws Exception {
    request = Helpers.defaultMockRequest();
    mockHttpServletResponse = new MockHttpServletResponse();
    startDate = new LocalDate(2013, 1, 1);
    endDate = new LocalDate(2013, 1, 31);
    json = IOUtils.toString(getClass().getResourceAsStream("/stat-json/stats-idp-sp.json"));
  }

  @Test
  public void testDownloadCsv() throws Exception {
    when(cruncher.getLoginsByIdp(startDate.toDate(), endDate.toDate(), "id")).thenReturn(json);

    LoginDataResponse dataResponse = controller.csvStats(startDate, endDate, null, request);

    assertEquals(7, dataResponse.getLoginData().size());
    assertEquals("statistics.csv", dataResponse.getFilename());
  }

}