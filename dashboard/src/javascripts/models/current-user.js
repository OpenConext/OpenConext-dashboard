class CurrentUser {
  constructor(rawUser) {
    this.attributeMap = rawUser.attributeMap;
    this.currentIdp = rawUser.currentIdp;
    this.dashboardAdmin = rawUser.dashboardAdmin;
    this.displayName = rawUser.displayName;
    this.grantedAuthorities = rawUser.grantedAuthorities;
    this.institutionIdps = rawUser.institutionIdps;
    this.statsUrl = rawUser.statsUrl;
    this.superUser = rawUser.superUser;
    this.switchedToIdp = rawUser.switchedToIdp;
    this.uid = rawUser.uid;
  }

  getCurrentIdp() {
    if (this.superUser && this.switchedToIdp) {
      return this.switchedToIdp;
    } else {
      return this.switchedToIdp || this.currentIdp;
    }
  }

  getCurrentIdpId() {
    return this.getCurrentIdp().id;
  }
}

export default CurrentUser;
