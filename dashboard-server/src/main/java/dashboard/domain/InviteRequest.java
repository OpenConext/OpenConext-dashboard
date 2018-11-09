package dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

public class InviteRequest {

    private String idpEntityId;
    private String idpName;
    private String spEntityId;
    private String spName;
    private String spId;
    private String message;

    private List<ContactPerson> contactPersons;

    public String getIdpEntityId() {
        return idpEntityId;
    }

    public void setIdpEntityId(String idpEntityId) {
        this.idpEntityId = idpEntityId;
    }

    public String getIdpName() {
        return idpName;
    }

    public void setIdpName(String idpName) {
        this.idpName = idpName;
    }

    public String getSpEntityId() {
        return spEntityId;
    }

    public void setSpEntityId(String spEntityId) {
        this.spEntityId = spEntityId;
    }

    public String getSpName() {
        return spName;
    }

    public void setSpName(String spName) {
        this.spName = spName;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }

    @JsonIgnore
    public boolean isContainsMessage() {
        return this.message != null;
    }

    @JsonIgnore
    public String getHtmlMessage() {
        return isContainsMessage() ? HtmlUtils.htmlEscape(message).replaceAll("\n", "<br/>") : "";
    }
}
