package nl.surfnet.coin.selfservice.control;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        final List<Action> actionList = jdbcTemplate.query("SELECT jiraKey, userId, userName, actionType, body, idp, sp FROM ss_actions WHERE institutionId = ?", new RowMapper<Action>() {
            @Override
            public Action mapRow(final ResultSet resultSet, final int i) throws SQLException {
                return new Action(
                        resultSet.getString("jiraKey"),
                        resultSet.getString("userId"),
                        resultSet.getString("userName"),
                        Action.Type.valueOf(resultSet.getString("actionType")),
                        Action.Status.valueOf(resultSet.getString("actionStatus")),
                        resultSet.getString("body"),
                        resultSet.getString("idp"),
                        resultSet.getString("sp"),
                        resultSet.getDate("requestDate"));
            }
        }, coinUser.getInstitutionId());

        model.put("actionList", actionList);

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
