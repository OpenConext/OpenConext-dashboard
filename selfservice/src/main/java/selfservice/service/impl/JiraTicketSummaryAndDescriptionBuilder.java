package selfservice.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static selfservice.domain.Action.Type.LINKREQUEST;
import static selfservice.domain.Action.Type.QUESTION;
import static selfservice.domain.Action.Type.UNLINKREQUEST;
import static selfservice.domain.Action.Type.CHANGE;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.MoreObjects;

import selfservice.domain.Action;
import selfservice.domain.LicenseStatus;
import selfservice.domain.ServiceProviderSettings;
import selfservice.domain.Settings;
import selfservice.domain.csa.ContactPerson;

class JiraTicketSummaryAndDescriptionBuilder {

  static SummaryAndDescription build(final Action action) {
    checkNotNull(action);

    StringBuilder description = new StringBuilder();

    final StringBuilder summary = new StringBuilder();


    if (action.getType().equals(CHANGE)) {
      Settings settings = action.getSettings();
      description.append("Please update settings to the following: \n").
        append("Keywords NL: ").append(settings.getKeywordsNl()).append("\n").
        append("Keywords EN: ").append(settings.getKeywordsEn()).append("\n").
        append("Published in eduGAIN: ").append(settings.isPublishedInEdugain()).append("\n");

      int index = 0;
      for (ContactPerson cp : settings.getContactPersons()) {
        description.append("\n");
        description.append("Contact Name ").append(index).append(": ").append(cp.getName()).append("\n");
        description.append("Contact Email ").append(index).append(": ").append(cp.getEmailAddress()).append("\n");
        description.append("Contact Telephone ").append(index).append(": ").append(cp.getTelephoneNumber()).append("\n");
        description.append("Contact Type ").append(index).append(": ").append(cp.getContactPersonType().toString()).append("\n");
        index++;
      }
      
      for (ServiceProviderSettings sp : settings.getServiceProviderSettings()) {
        description.append("\n");
        description.append("Service Provider ID: ").append(sp.getSpEntityId()).append("\n");
        description.append("Service Provider Guest Login Enabled: ").append(sp.isHasGuestEnabled()).append("\n");
        description.append("Service Provider No Consent Required: ").append(sp.isNoConsentRequired()).append("\n");
        description.append("Service Provider Published In Edugain: ").append(sp.isPublishedInEdugain()).append("\n");
        description.append("Service Provider Description EN: ").append(sp.getDescriptionEn()).append("\n");
        description.append("Service Provider Description NL: ").append(sp.getDescriptionNl()).append("\n");
      }
      description.append("\n");
      
      description.append("Additional comments: ").append(settings.getComments()).append("\n");
      
      description.append("\n");
      
      summary.append("Change settings for ").append(action.getIdpId());
    } else if (action.getType().equals(QUESTION)) {
      description.append("Question: ").append(action.getBody()).append("\n");
      summary.
        append("Question about ").
        append(action.getSpId());
    } else if (LINKREQUEST.equals(action.getType())) {
      description.append("Request: Create a new connection").append("\n");
      summary.
        append("New connection for IdP ").
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
      description.append("License secured: ").append(licenseSecured(action)).append("\n");
    }
    description.append("Time: ").append(new SimpleDateFormat("HH:mm dd-MM-yy").format(new Date())).append("\n");

    if (!action.getType().equals(QUESTION)) {
      description.append("Remark from user: ").append(action.getBody()).append("\n");
    }

    return new SummaryAndDescription(summary.toString(), description.toString());
  }

  static String licenseSecured(Action action) {
    LicenseStatus status = action.getService().getLicenseStatus();

    switch(status) {
      case HAS_LICENSE_SP:
        return "Unknown";
      case HAS_LICENSE_SURFMARKET:
          return "Yes";
      case NOT_NEEDED:
        return "n/a";
      default:
        return "Unknown";
    }
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
