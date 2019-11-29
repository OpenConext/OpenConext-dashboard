package dashboard.domain;


public class LoaLevelChange {

    private String entityId;
    private String loaLevel;
    private String entityType;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getLoaLevel() {
        return loaLevel;
    }

    public void setLoaLevel(String loaLevel) {
        this.loaLevel = loaLevel;
    }
}
