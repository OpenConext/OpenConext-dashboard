package selfservice.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static selfservice.domain.JiraTask.Type.LINKREQUEST;
import static selfservice.domain.JiraTask.Type.QUESTION;
import static selfservice.domain.JiraTask.Type.UNLINKREQUEST;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.MoreObjects;

import selfservice.domain.JiraTask;
import selfservice.domain.CoinUser;

class JiraTicketSummaryAndDescriptionBuilder {

  static SummaryAndDescription build(final JiraTask task, final CoinUser user) {
    checkNotNull(task.getIssueType());
    checkNotNull(user);

    StringBuilder description = new StringBuilder();

    final StringBuilder summary = new StringBuilder();

    if (task.getIssueType().equals(QUESTION)) {
      description.append("Question: ").append(task.getBody()).append("\n");
      summary.
        append("Question about ").
        append(task.getServiceProvider());
    } else if (LINKREQUEST.equals(task.getIssueType())) {
      description.append("Request: Create a new connection").append("\n");
      summary.
        append("New connection for IdP ").
        append(task.getIdentityProvider()).
        append(" to SP ").
        append(task.getServiceProvider());
    } else if (UNLINKREQUEST.equals(task.getIssueType())) {
      description.append("Request: terminate a connection").append("\n");
      summary.
        append("Disconnect IdP ").
        append(task.getIdentityProvider()).
        append(" and SP ").
        append(task.getServiceProvider());
    } else {
      throw new IllegalArgumentException("Don't know how to handle tasks of type " + task.getIssueType());
    }

    description.append("Applicant name: ").append(user.getDisplayName()).append("\n");
    description.append("Applicant email: ").append(user.getEmail()).append("\n");
    description.append("Identity Provider: ").append(task.getIdentityProvider()).append("\n");
    description.append("Service Provider: ").append(task.getServiceProvider()).append("\n");
    description.append("Time: ").append(new SimpleDateFormat("HH:mm dd-MM-yy").format(new Date())).append("\n");
    description.append("Service Provider: ").append(task.getServiceProvider()).append("\n");

    if (!task.getIssueType().equals(QUESTION)) {
      description.append("Remark from user: ").append(task.getBody()).append("\n");
    }

    return new SummaryAndDescription(summary.toString(), description.toString());
  }

  static class SummaryAndDescription {
    public final String summary;
    public final String description;

    public SummaryAndDescription(String summary, String description) {
      this.summary = summary;
      this.description = description;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(SummaryAndDescription.class)
          .add("summary", summary)
          .add("description", description).toString();
    }
  }
}
