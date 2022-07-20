package dashboard.manage;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@Getter
public class ChangeRequest implements Serializable {

    private String metaDataId;
    private String type;
    private String note;
    private Map<String, Object> pathUpdates;
    private Map<String, Object> auditData;

    private boolean incrementalChange;

    private PathUpdateType pathUpdateType;

    public ChangeRequest(String metaDataId, String type, Map<String, Object> pathUpdates, Map<String, Object> auditData, boolean incrementalChange, PathUpdateType pathUpdateType) {
        this.metaDataId = metaDataId;
        this.type = type;
        this.note = auditData != null ? (String) auditData.get("notes") : null;
        this.pathUpdates = pathUpdates;
        this.auditData = auditData;
        this.incrementalChange = incrementalChange;
        this.pathUpdateType = pathUpdateType;
    }

    public void setAuditData(Map<String, Object> auditData) {
        this.auditData = auditData;
        this.note = (String) auditData.get("notes");
    }
}
