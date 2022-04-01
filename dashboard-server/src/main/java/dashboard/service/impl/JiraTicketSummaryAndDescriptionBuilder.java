package dashboard.service.impl;

import com.google.common.base.MoreObjects;
import dashboard.domain.Action;
import dashboard.domain.Change;
import dashboard.domain.Settings;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static dashboard.domain.Action.Type.*;

class JiraTicketSummaryAndDescriptionBuilder {

    static SummaryAndDescription build(final Action action) {
        checkNotNull(action);

        final StringBuilder description = new StringBuilder();

        final StringBuilder summary = new StringBuilder();

        if (!CollectionUtils.isEmpty(action.getManageUrls())) {
            description.append("A change request in manage has been created to merge this user request.See:\n" +
                    String.join("\n", action.getManageUrls()) + "\n\n");
        }

        if (action.getType().equals(CHANGE)) {
            Settings settings = action.getSettings();
            if (settings != null && StringUtils.hasText(settings.getComments())) {
                description.append("Additional comments: ").append(settings.getComments()).append("\n");
            }
            description.append("\n");

            summary.append("Change settings for ").append(action.getIdpId());
        } else if (LINKREQUEST.equals(action.getType())) {
            description.append("Request: Create a new connection").append("\n");
            summary.
                    append("New connection for IdP ").
                    append(action.getIdpId()).
                    append(" to SP ").
                    append(action.getSpId());
            if (action.getLoaLevel() != null) {
                description
                        .append("\n")
                        .append("Note: the user has requested a higher LOA level then default, ")
                        .append(action.getLoaLevel())
                        .append("\n");
            }
        } else if (LINKINVITE.equals(action.getType())) {
            description.append("Invite request: If the SCV accepts this invitation then create a new connection by clicking the link to manage in the comments").append("\n");
            summary.
                    append("New invite connection for IdP ").
                    append(action.getIdpId()).
                    append(" to SP ").
                    append(action.getSpId());
        } else if (UNLINKREQUEST.equals(action.getType())) {
            description.append("Request: terminate a connection").append("\n");
            summary.
                    append("Disconnect IdP ").
                    append(action.getIdpId()).
                    append(" and SP ").
                    append(action.getSpId());
        } else {
            throw new IllegalArgumentException("Don't know how to handle tasks of type " + action.getType());
        }

        description.append("Applicant name: ").append(action.getUserName()).append("\n");
        description.append("Applicant email: ").append(action.getUserEmail()).append("\n");
        description.append("Identity Provider: ").append(action.getIdpId()).append("\n");
        description.append("Service Provider: ").append(action.getSpId()).append("\n");
        if (action.getService() != null && action.getService().getLicenseStatus() != null) {
            description.append("License required: ").append(action.getService().getLicenseStatus().getName()).append("\n");
        }
        if (action.getService() != null && action.getService().isAansluitovereenkomstRefused() && LINKREQUEST.equals(action.getType())) {
            description.append("Customer accepts connecting despite aansluitovereenkomst refused").append("\n");
        }
        description.append("Time: ").append(new SimpleDateFormat("HH:mm dd-MM-yy").format(new Date())).append("\n");

        description.append("Remark from user: ").append(action.getBody()).append("\n");

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
