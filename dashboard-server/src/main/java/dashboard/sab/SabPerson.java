package dashboard.sab;

import java.util.List;

import static java.lang.String.format;

public class SabPerson {
    private final String firstName;
    private final String middleName;
    private final String surname;
    private final String uid;
    private final String email;
    private final List<SabRole> roles;

    public SabPerson(String firstName, String middleName, String surname, String uid, String email, List<SabRole> roles) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.surname = surname;
        this.uid = uid;
        this.email = email;
        this.roles = roles;
    }

    public String fullname() {
        if (middleName.isEmpty()) {
            return format("%s %s", firstName, surname);
        }
        return format("%s %s %s", firstName, middleName, surname);
    }

    public boolean hasRole(final String roleName) {
        return roles.stream().anyMatch(role -> roleName.equals(role.roleName));
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getSurname() {
        return surname;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public List<SabRole> getRoles() {
        return roles;
    }
}
