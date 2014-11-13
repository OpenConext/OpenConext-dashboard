package nl.surfnet.coin.selfservice.util;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static nl.surfnet.coin.selfservice.util.AttributeMapFilter.filterAttributes;
import static nl.surfnet.coin.selfservice.util.AttributeMapFilter.valuesToShow;
import static org.junit.Assert.*;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;

public class AttributeMapFilterTest {

  private Map<String, List<Object>> serviceAttributes;
  private Map<String, List<String>> userAttributes;

  @Before
  public void setUp() throws Exception {
    serviceAttributes = new HashMap<>();
    userAttributes = new HashMap<>();
  }

  @Test
  public void test_no_match() throws Exception {
    List<String> result = valuesToShow(asList("foo"), asList("bar"));
    assertEquals(0, result.size());
  }

  @Test
  public void test_exact_match() throws Exception {
    List<String> result = valuesToShow(asList("bar"), asList("bar", "foo"));
    assertThat(result, hasItems("bar"));
  }

  @Test
  public void test_no_filters() throws Exception {
    List<String> result = valuesToShow(new ArrayList<String>(), asList("bar"));
    assertThat(result, hasItems("bar"));
  }

  @Test
  public void test_wildcard() throws Exception {
    List<String> result = valuesToShow(asList("*"), asList("bar", "foo"));
    assertThat(result, hasItems("bar", "foo"));
  }

  @Test
  public void test_with_colons() throws Exception {
    List<String> result = valuesToShow(asList("foo:bar"), asList("foo:bar"));
    assertThat(result, hasItems("foo:bar"));
  }

  @Test
  public void test_with_colons_and_wildcard() throws Exception {
    List<String> result = valuesToShow(asList("foo:*"), asList("foo:bar", "foo:foo", "bar:foo"));
    assertThat(result, hasItems("foo:bar", "foo:foo"));
  }

  @Test
  public void test_with_colons_and_wildcard_in_the_middle() throws Exception {
    List<String> result = valuesToShow(asList("foo:*:bar"), asList("foo:bar", "foo:foo:bar", "foo:foo:bar:bar"));
    assertThat(result, hasItems("foo:foo:bar", "foo:foo:bar:bar"));
  }

  @Test
  public void test_filter_attributes_returns_empty_list_when_no_service_attributes() throws Exception {
    Collection<AttributeMapFilter.ServiceAttribute> actual = filterAttributes(serviceAttributes, userAttributes);
    assertEquals(0, actual.size());
  }

  @Test
  public void test_filter_attributes_returns_service_attributes_when_nothing_to_filter() throws Exception {
    serviceAttributes.put("foo", asList((Object)"bar"));
    Collection<AttributeMapFilter.ServiceAttribute> actual = filterAttributes(serviceAttributes, userAttributes);
    assertThat(actual, hasItems(new AttributeMapFilter.ServiceAttribute("foo", "bar")));
  }

  @Test
  public void test_filter_attributes_returns_filtered_list_of_service_attributes_with_user_attributes() throws Exception {
    serviceAttributes.put("foo", asList((Object)"*"));
    userAttributes.put("foo", asList("bar", "foobar"));
    userAttributes.put("bar", asList("bar"));
    AttributeMapFilter.ServiceAttribute expectedServiceAttribute = new AttributeMapFilter.ServiceAttribute("foo", "*");
    expectedServiceAttribute.addUserValues("bar", "foobar");

    Collection<AttributeMapFilter.ServiceAttribute> actual = filterAttributes(serviceAttributes, userAttributes);

    assertThat(actual, hasItems(expectedServiceAttribute));
  }
}
