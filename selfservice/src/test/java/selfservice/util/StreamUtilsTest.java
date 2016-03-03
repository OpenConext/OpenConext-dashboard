package selfservice.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class StreamUtilsTest {

  @Test
  public void testFilterEmpty() {
    List<Optional<String>> optionals = Arrays.asList(Optional.<String>empty(), Optional.of("1"), Optional.<String>empty(), Optional.of("2"));
    List<String> result = optionals.stream().collect(StreamUtils.filterEmpty());
    assertEquals(Arrays.asList("1", "2"), result);
  }

}
