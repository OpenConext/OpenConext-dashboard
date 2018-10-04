package selfservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;

public abstract class StreamUtils {

  public static <T> Collector<Optional<T>, List<T>, List<T>> filterEmpty() {
    return Collector.of(ArrayList::new, (container, value) -> {
        if (value.isPresent()) {
          container.add(value.get());
        }
      }, (left, right) -> left, list -> list
    );
  }

}
