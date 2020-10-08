let currentUser = null;

class CurrentUser {
    constructor(rawUser) {
        this.attributeMap = rawUser.attributeMap;
        this.currentIdp = rawUser.currentIdp;
        this.dashboardAdmin = rawUser.dashboardAdmin;
        this.dashboardViewer = rawUser.dashboardViewer;
        this.dashboardMember = rawUser.dashboardMember;
        this.displayName = rawUser.displayName;
        this.grantedAuthorities = rawUser.grantedAuthorities;
        this.institutionIdps = rawUser.institutionIdps;
        this.statsUrl = rawUser.statsUrl;
        this.superUser = rawUser.superUser;
        this.switchedToIdp = rawUser.switchedToIdp;
        this.uid = rawUser.uid;
        this.statsEnabled = rawUser.statsEnabled;
        this.manageConsentEnabled = rawUser.manageConsentEnabled;
        this.oidcEnabled = rawUser.oidcEnabled;
        this.hideTabs = rawUser.hideTabs;
        this.supportedLanguages = rawUser.supportedLanguages;
        this.organization = rawUser.organization;
        this.guest = rawUser.guest;
        this.loaLevels = rawUser.loaLevels;
        this.defaultLoa = rawUser.defaultLoa;
        this.email = rawUser.email;
    }


    getCurrentIdp() {
        if (this.guest) {
            return {state: "prodaccepted"};
        }
        if (this.superUser && this.switchedToIdp) {
            return this.switchedToIdp;
        }

        return this.switchedToIdp || this.currentIdp;
    }

    getCurrentIdpId() {
        return this.getCurrentIdp().id;
    }

}

export const createCurrentUser = payload => {
    currentUser = new CurrentUser(payload);
    return currentUser;
};

export const getCurrentUser = () => currentUser;

export default CurrentUser;
