package selfservice.service.impl;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static selfservice.service.impl.JiraTicketSummaryAndDescriptionBuilder.build;

import org.junit.Test;

import selfservice.domain.JiraTask;
import selfservice.domain.JiraTask.Type;
import selfservice.domain.CoinUser;
import selfservice.service.impl.JiraTicketSummaryAndDescriptionBuilder.SummaryAndDescription;

public class JiraTicketSummaryAndDescriptionBuilderTest {

  @Test
  public void a_question_should_contain_the_actual_question_in_the_description() {
    JiraTask task = new JiraTask.Builder().issueType(Type.QUESTION).body("my question").build();
    CoinUser user = new CoinUser();

    SummaryAndDescription summaryAndDescription = build(task, user);

    assertThat(summaryAndDescription.description, containsString("Question: my question"));
  }

  @Test
  public void a_request_should_contain_the_user_remarks() {
    JiraTask task = new JiraTask.Builder().issueType(Type.LINKREQUEST).body("my remarks").build();
    CoinUser user = new CoinUser();

    SummaryAndDescription summaryAndDescription = build(task, user);

    assertThat(summaryAndDescription.description, containsString("Remark from user: my remarks"));
  }
}
