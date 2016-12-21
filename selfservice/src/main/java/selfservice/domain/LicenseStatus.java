package selfservice.domain;

public enum LicenseStatus {

    HAS_LICENSE_SURFMARKET("Yes, with SURFmarket"),
    HAS_LICENSE_SP("Yes, with service provider"),
    NOT_NEEDED("Not needed"),
    UNKNOWN("Unknown");

    private String name;

    LicenseStatus(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
