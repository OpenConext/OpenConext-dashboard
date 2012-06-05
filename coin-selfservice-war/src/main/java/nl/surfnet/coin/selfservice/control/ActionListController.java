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

package nl.surfnet.coin.selfservice.control;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import nl.surfnet.coin.selfservice.domain.Action;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.service.ActionsService;
import nl.surfnet.coin.selfservice.service.JiraService;

@Controller
public class ActionListController extends BaseController {

    private JdbcTemplate jdbcTemplate;

    @Resource(name="jiraService")
    JiraService jiraService;

    @Resource(name="actionsService")
    private ActionsService actionsService;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @RequestMapping(value="actions")
    public ModelAndView listActions() throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();

        CoinUser coinUser = getCurrentUser();

        final String institutionId = coinUser.getInstitutionId();

        actionsService.synchronizeWithJira(institutionId);
        final List<Action> actions = actionsService.getActions(institutionId);
        model.put("actionList", actions);

        return new ModelAndView("actions", model);
    }

    @RequestMapping(value="create-question", method = RequestMethod.POST)
    public View createQuestion(
            @RequestParam String body
    ) throws IOException {
        CoinUser coinUser = getCurrentUser();
        JiraTask jiraTask = new JiraTask.Builder()
                .body(body)
                .institution(coinUser.getInstitutionId())
                .build();
        String jiraKey = jiraService.create(jiraTask);
        jdbcTemplate.update(
                "INSERT INTO ss_actions(jiraKey, userId, userName, institutionId, actionType, actionStatus, body) VALUES(?, ?, ?, ?, ?, 'QUESTION', 'OPEN', ?)",
                jiraKey, coinUser.getUid(), coinUser.getDisplayName(), coinUser.getInstitutionId(), body);
        return new RedirectView("actions.shtml");
    }

    @RequestMapping(value="create-request")
    public View createRequest(
            @RequestParam String idp,
            @RequestParam String sp,
            @RequestParam String body
    ) throws IOException {
        CoinUser coinUser = getCurrentUser();
        JiraTask jiraTask = new JiraTask.Builder()
                .body(body)
                .institution(coinUser.getInstitutionId())
                .identityProvider(idp)
                .serviceProvider(sp)
                .build();
        String jiraKey = jiraService.create(jiraTask);
        jdbcTemplate.update(
                "INSERT INTO ss_actions(jiraKey, userId, userName, institutionId, actionType, actionStatus, body, idp, sp) VALUES(?, ?, ?, ?, ?, 'REQUEST', 'OPEN', ?, ?, ?)",
                jiraKey, coinUser.getUid(), coinUser.getDisplayName(), coinUser.getInstitutionId(), body, sp, idp);
        return new RedirectView("actions.shtml");
    }


}
