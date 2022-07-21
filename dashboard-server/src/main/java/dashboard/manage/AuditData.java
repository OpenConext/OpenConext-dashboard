package dashboard.manage;

import dashboard.domain.CoinUser;
import dashboard.util.SpringSecurity;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AuditData {

    private AuditData() {
    }

    public static Map<String, Object> context(String action, String jiraKey) {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        String now = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());
        String notes = String.format(
                "%s by request of user %s %s from IdP %s on %s via dashboard (%s)",
                action,
                currentUser.getGivenName(),
                currentUser.getSurname(),
                currentUser.getIdp().getName(),
                now,
                StringUtils.hasText(jiraKey) ? jiraKey : "No JIRA ticket"
        );
        return Map.of(
                "user", currentUser.getUid(),
                "notes", notes
        );
    }
}
