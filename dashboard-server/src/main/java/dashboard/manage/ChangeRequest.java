package dashboard.manage;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChangeRequest implements Serializable {

    private String metaDataId;
    private String type;
    private String note;
    private Map<String, Object> pathUpdates;
    private Map<String, Object> auditData;


}
