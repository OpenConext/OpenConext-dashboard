package selfservice.domain.csa;

import java.util.List;

public class CheckTokenResponse {
  private final String idPEntityId;
  private List<String> scopes;

  public CheckTokenResponse(String idPEntityId, List<String> scopes) {
    this.idPEntityId = idPEntityId;
    this.scopes = scopes;
  }

  public String getIdPEntityId() {
    return idPEntityId;
  }

  public List<String> getScopes() {
    return scopes;
  }
}
