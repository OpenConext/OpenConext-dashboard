package selfservice.stats;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Stats {

    List<Map<String, Object>> loginTimeFrame(long from, long to, String scale, Optional<String> spEntityId, String state);

    List<Map<String, Object>> loginAggregated(String period, Optional<String> spEntityId, String state);

    List<Map<String, Object>> uniqueLoginCount(long from, long to, String spEntityId, String state);

}

