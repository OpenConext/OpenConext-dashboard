/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package csa.service.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import csa.dao.ActionsDao;
import csa.model.Action;
import csa.model.JiraTask;
import csa.model.Statistics;
import csa.service.StatisticsService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StatisticsServiceImplTest {

  @InjectMocks
  private StatisticsService statisticsService;
  
  @Mock
  private ActionsDao actionDaoMock;
  
  @Before
  public void setup() {
    statisticsService = new StatisticsServiceImpl();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testBasicRetrievalOfStatistics() {
    Calendar from = new GregorianCalendar(2012, 9, 1);
    Calendar to = new GregorianCalendar(2012, 9, 1);
    to.setLenient(true);
    to.add(Calendar.MONTH, 1);
    
    List<Action> actions = new ArrayList<Action>();
    Action action = new Action("key", "userid", "username", "john.doe@nl", JiraTask.Type.QUESTION, JiraTask.Status.OPEN, "body", "idp", "sp", "institute-123", new Date());
    actions.add(action);
    when(actionDaoMock.findActionsByDateRange(from.getTime(), to.getTime())).thenReturn(actions);
    Statistics result = statisticsService.getStatistics(10, 2012);
    
    assertNotNull(result);
    assertEquals("total number of questions is wrong", 1, result.getTotalQuestions());
    assertEquals("total number of questions is wrong", 0, result.getTotalLinkRequests());
    assertEquals("total number of questions is wrong", 0, result.getTotalUnlinkRequests());
    assertNotNull(result.getInstitutionLinkRequests());
    assertNotNull(result.getInstitutionQuestions());
    assertNotNull(result.getInstitutionUnlinkRequests());
    assertEquals("Institution specific questions is wrong", new Integer(1), result.getInstitutionQuestions().get("institute-123"));
  }
}
