package dashboard.service.impl;

import dashboard.domain.Action;
import dashboard.domain.LicenseStatus;
import dashboard.domain.Service;
import dashboard.service.impl.JiraTicketSummaryAndDescriptionBuilder.SummaryAndDescription;
import org.junit.Test;

import java.util.Collections;

import static dashboard.service.impl.JiraTicketSummaryAndDescriptionBuilder.build;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class JiraTicketSummaryAndDescriptionBuilderTest {

    @Test
    public void a_request_should_contain_the_user_remarks() {
        Service service = new Service(1, "test", "test", "test", "test");
        service.setLicenseStatus(LicenseStatus.UNKNOWN);

        Action action = Action.builder().type(Action.Type.LINKREQUEST).body("my remarks").service(service).build();

        SummaryAndDescription summaryAndDescription = build(action, Collections.emptyList());

        assertThat(summaryAndDescription.description, containsString("Remark from user: my remarks"));
    }
}
