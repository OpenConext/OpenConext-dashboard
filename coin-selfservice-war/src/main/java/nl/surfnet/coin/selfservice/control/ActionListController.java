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
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.service.JiraService;

@Controller
public class ActionListController {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    JiraService jiraService;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @RequestMapping(value="actions")
    public ModelAndView listActions() {
        Map<String, Object> model = new HashMap<String, Object>();

        CoinUser coinUser = getCurrentUser();


//        model.put("actionList", actionList);

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

    /**
     * Get the IDP Entity Id from the security context.
     * @return String
     * @throws SecurityException in case no principal is found.
     */
    private static CoinUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new SecurityException("No suitable security context.");
        }
        Object principal = auth.getPrincipal();
        if (principal != null && principal instanceof CoinUser) {
            return (CoinUser) principal;
        }
        throw new SecurityException("No suitable security context.");
    }


}
