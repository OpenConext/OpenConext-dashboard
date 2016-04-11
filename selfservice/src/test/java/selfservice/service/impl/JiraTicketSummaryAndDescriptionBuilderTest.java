package selfservice.service.impl;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static selfservice.service.impl.JiraTicketSummaryAndDescriptionBuilder.build;

import org.junit.Test;

import selfservice.domain.Action;
import selfservice.service.impl.JiraTicketSummaryAndDescriptionBuilder.SummaryAndDescription;

public class JiraTicketSummaryAndDescriptionBuilderTest {

  @Test
  public void a_question_should_contain_the_actual_question_in_the_description() {
    Action action = Action.builder().type(Action.Type.QUESTION).body("my question").build();

    SummaryAndDescription summaryAndDescription = build(action);

    assertThat(summaryAndDescription.description, containsString("Question: my question"));
  }

  @Test
  public void a_request_should_contain_the_user_remarks() {
    Action action = Action.builder().type(Action.Type.LINKREQUEST).body("my remarks").build();

    SummaryAndDescription summaryAndDescription = build(action);

    assertThat(summaryAndDescription.description, containsString("Remark from user: my remarks"));
  }
}
