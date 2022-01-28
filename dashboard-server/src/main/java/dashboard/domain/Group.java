package dashboard.domain;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Group implements Serializable {

    private final String id;

    public Group(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(Group.class).add("id", id).toString();
    }

}
