package dashboard.stats;

import java.util.List;
import java.util.Optional;

public interface Stats {

    List<Object> loginTimeFrame(long from, long to, String scale, Optional<String> spEntityId, String state);

    List<Object> loginAggregated(String period, Optional<String> spEntityId, String state);

    List<Object> uniqueLoginCount(long from, long to, String spEntityId, String state);

}

