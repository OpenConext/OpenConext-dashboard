package dashboard.util;

public class Constants {
    /**
     * The name of the key under which all the identity providers are stored
     * for the identity switch view
     */
    public static final String INSTITUTION_IDENTITY_PROVIDERS = "institutionIdentityProviders";

    /**
     * The name of the key under which we store the token used to prevent session
     * hijacking
     */
    public static final String TOKEN_CHECK = "tokencheck";

    /**
     * The name of the key under which the switched identity is stored
     */
    public static final String SWITCHED_IDENTITY_SWITCH = "switchedIdentitySwitch";

    /**
     * The name of the key under which we store the info if a logged user is
     * allowed to request connections / disconnects
     */
    public static final String SERVICE_APPLY_ALLOWED = "applyAllowed";

    /**
     * The name of the key under which we store the info if a logged user is
     * allowed to ask questions
     */
    public static final String SERVICE_QUESTION_ALLOWED = "questionAllowed";

    /**
     * The name of the key under which we store the info if the status of a
     * technical connection is visible to the current user.
     */
    public static final String SERVICE_CONNECTION_VISIBLE = "connectionVisible";

    public static final String SHOW_ARP_MATCHES_PROVIDED_ATTRS = "showArpMatchesProvidedAttrs";

    public static final String SERVICE = "service";
}
