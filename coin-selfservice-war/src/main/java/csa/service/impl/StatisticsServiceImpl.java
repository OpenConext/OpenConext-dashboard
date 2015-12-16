package csa.service.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import csa.model.Statistics;
import csa.dao.ActionsDao;
import csa.model.Action;
import csa.service.StatisticsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service(value = "statisticsService")
public class StatisticsServiceImpl implements StatisticsService {
  private static final Logger LOG = LoggerFactory.getLogger(StatisticsServiceImpl.class);

  @Resource(name="actionsDao")
  private ActionsDao actionsDao;

  @Override
  public Statistics getStatistics(int month, int year) {
    Statistics result = new Statistics();
    Calendar from = new GregorianCalendar(year, month-1, 1);
    Calendar to = new GregorianCalendar(year, month-1, 1);
    to.add(Calendar.MONTH, 1);
    LOG.info("returning statistics for {} {} ({} - {})",month, year, from.getTime(), to.getTime());
    List<Action> actions = actionsDao.findActionsByDateRange(from.getTime(), to.getTime());
    if (null != actions) {
      for (Action a : actions) {
        String institutionId = a.getInstitutionId();
        switch(a.getType()) {
        case QUESTION :
          result.countTotalQuestions();
          result.countInstitutionQuestion(institutionId);
          break;
        case LINKREQUEST :
          result.countTotalLinkRequests();
          result.countInstitutionLinkRequest(institutionId);
          break;
        case UNLINKREQUEST :
          result.countTotalUnlinkRequests();
          result.countInstitutionUnlinkRequest(institutionId);
          break;
        default :
            LOG.error("Switch statement entered default state (this cannot happen)");
        }
      }
    }
    return result;
  }
}
