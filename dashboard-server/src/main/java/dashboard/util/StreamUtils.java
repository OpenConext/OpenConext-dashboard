package dashboard.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;

public abstract class StreamUtils {

    public static <T> Collector<Optional<T>, List<T>, List<T>> filterEmpty() {
        return Collector.of(ArrayList::new, (container, value) -> {
            value.ifPresent(container::add);
                }, (left, right) -> left, list -> list
        );
    }

}
