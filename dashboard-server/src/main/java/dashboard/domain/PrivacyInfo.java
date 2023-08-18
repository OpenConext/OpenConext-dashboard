package dashboard.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PrivacyInfo implements Serializable {

    private final String whatData;
    private final String country;
    private final String accessData;
    private final String securityMeasures;
    private final String privacyStatementURLen;
    private final String privacyStatementURLnl;
    private final String dpaType;
    private final String otherInfo;

    public PrivacyInfo(String whatData,
                       String country,
                       String accessData,
                       String securityMeasures,
                       String privacyStatementURLen,
                       String privacyStatementURLnl,
                       String dpaType,
                       String otherInfo) {
        this.whatData = whatData;
        this.country = country;
        this.accessData = accessData;
        this.securityMeasures = securityMeasures;
        this.privacyStatementURLen = privacyStatementURLen;
        this.privacyStatementURLnl = privacyStatementURLnl;
        this.dpaType = dpaType;
        this.otherInfo = otherInfo;

    }
}
