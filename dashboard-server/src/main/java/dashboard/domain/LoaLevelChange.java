package dashboard.domain;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoaLevelChange {

    private String entityId;
    private String loaLevel;
    private String entityType;

}
