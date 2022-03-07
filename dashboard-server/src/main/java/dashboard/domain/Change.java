package dashboard.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Change {

    private final String entityId;
    private final String attribute;
    private final String oldValue;
    private final String newValue;

}
