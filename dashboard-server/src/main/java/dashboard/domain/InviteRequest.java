package dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

public class InviteRequest {

    private String idpEntityId;
    private String idpName;
    private String spEntityId;
    private String spName;
    private String spId;
    private String typeMetaData;
    private String message;
    private boolean connectionRequest;

    private List<ContactPerson> contactPersons;

    public InviteRequest() {
    }

    public InviteRequest(ServiceConnectionRequest serviceConnectionRequest, String spId, String idpName, String spName, List<ContactPerson> contactPersons) {
        this.idpEntityId = serviceConnectionRequest.getIdpEntityId();
        this.spEntityId = serviceConnectionRequest.getSpEntityId();
        this.spId = spId;
        this.typeMetaData = serviceConnectionRequest.getTypeMetaData();
        this.idpName = idpName;
        this.spName = spName;
        this.contactPersons = contactPersons;
    }

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

    public String getTypeMetaData() {
        return typeMetaData;
    }

    public void setTypeMetaData(String typeMetaData) {
        this.typeMetaData = typeMetaData;
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

    public boolean isConnectionRequest() {
        return connectionRequest;
    }

    public void setConnectionRequest(boolean connectionRequest) {
        this.connectionRequest = connectionRequest;
    }

    @JsonIgnore
    public boolean isContainsMessage() {
        return StringUtils.hasText(this.message);
    }

    @JsonIgnore
    public String getHtmlMessage() {
        return isContainsMessage() ? HtmlUtils.htmlEscape(message).replaceAll("\n", "<br/>") : "";
    }
}
