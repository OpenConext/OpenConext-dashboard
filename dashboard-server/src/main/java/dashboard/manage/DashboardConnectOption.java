package dashboard.manage;

import org.apache.commons.lang3.ArrayUtils;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public enum DashboardConnectOption {
    CONNECT_WITH_INTERACTION("connect_with_interaction"),
    CONNECT_WITHOUT_INTERACTION_WITH_EMAIL("connect_without_interaction_with_email"),
    CONNECT_WITHOUT_INTERACTION_WITHOUT_EMAIL("connect_without_interaction_without_email");

    private String option;

    DashboardConnectOption(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }

    public boolean connectsWithoutInteraction() {
        return ArrayUtils.contains(
                new String[]{CONNECT_WITHOUT_INTERACTION_WITH_EMAIL.getOption(),
                        CONNECT_WITHOUT_INTERACTION_WITHOUT_EMAIL.getOption()}, this.option);
    }

    public boolean sendsEmail() {
        return this.option.equals(CONNECT_WITHOUT_INTERACTION_WITH_EMAIL.getOption());
    }

    public static DashboardConnectOption fromOption(String option) {
        String sanitizedType = option.replaceAll(Pattern.quote("-"), "_");
        return Stream.of(DashboardConnectOption.values())
                .filter(dashboardConnectOption -> dashboardConnectOption.getOption().equals(sanitizedType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid EntityType " + option));

    }
}
