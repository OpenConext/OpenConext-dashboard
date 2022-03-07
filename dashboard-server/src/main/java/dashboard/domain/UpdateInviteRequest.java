package dashboard.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInviteRequest {

    public enum Status {
        ACCEPTED, REJECTED
    }

    private String jiraKey;
    private Status status;
    private String comment;
    private String loaLevel;
    private String spEntityId;
    private String typeMetaData;
    private boolean connectWithoutInteraction;

}
