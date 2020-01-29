package dashboard.domain;


public class ServiceConnectionRequest {
    private String idpEntityId;
    private String spEntityId;
    private String ownName;
    private String ownEmail;
    private String typeMetaData;

    public String getIdpEntityId() {
        return idpEntityId;
    }

    public String getSpEntityId() {
        return spEntityId;
    }

    public String getOwnName() {
        return ownName;
    }

    public String getOwnEmail() {
        return ownEmail;
    }

    public String getTypeMetaData() {
        return typeMetaData;
    }
}
