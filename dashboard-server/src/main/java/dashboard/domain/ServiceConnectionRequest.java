package dashboard.domain;

import lombok.Getter;

@Getter
public class ServiceConnectionRequest {

    private String idpEntityId;
    private String spEntityId;
    private String ownName;
    private String ownEmail;
    private String typeMetaData;

}
