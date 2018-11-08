package dashboard.domain;

import java.util.List;

public class InviteRequest {

    private String idpEntityId;
    private String sPEntityId;
    private List<ContactPerson> contactPersons;

    public String getIdpEntityId() {
        return idpEntityId;
    }

    public void setIdpEntityId(String idpEntityId) {
        this.idpEntityId = idpEntityId;
    }

    public String getsPEntityId() {
        return sPEntityId;
    }

    public void setsPEntityId(String sPEntityId) {
        this.sPEntityId = sPEntityId;
    }

    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }
}
