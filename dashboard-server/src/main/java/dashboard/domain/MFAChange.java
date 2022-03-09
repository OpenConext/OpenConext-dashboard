package dashboard.domain;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MFAChange {

    private String entityId;
    private String authnContextLevel;
    private String entityType;

}
