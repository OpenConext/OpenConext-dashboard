package dashboard.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Optional;

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
    private String type;

    public Optional<String> getOptionalLoaLevel() {
     return StringUtils.hasText(loaLevel) ? Optional.of(loaLevel) : Optional.empty();
    }

}
