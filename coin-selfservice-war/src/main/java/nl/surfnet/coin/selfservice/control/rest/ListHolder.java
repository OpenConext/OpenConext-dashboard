package nl.surfnet.coin.selfservice.control.rest;

import java.util.List;

/**
 * Wraps Lists in an Object for CSRF vulnerability.
 * See e.g. http://stackoverflow.com/questions/3503102/what-are-top-level-json-arrays-and-why-are-they-a-security-risk
 */
public class ListHolder<T> {
  private List<T> result;

  public ListHolder(List<T> result) {
    this.result = result;
  }

  public List<T> getResult() {
    return result;
  }
}
