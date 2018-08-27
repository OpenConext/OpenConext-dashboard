import React from "react";

class MatchStartRoute extends React.Component {
    componentDidMount() {
        //the redirect_uri goes to /, but we have stored the requested href in the state parameter
        let pathname = "/apps";
        const locationHash = window.location.hash.substr(1);
        const url = locationHash.substr(locationHash.indexOf("state=")).split("&")[0].split("=")[1];
        if (url) {
            const parser = document.createElement("a");
            parser.href = decodeURIComponent(url);
            pathname = parser.pathname;
            pathname = pathname[0] === "/" ? pathname : "/" + pathname;
        }
        if (pathname === "/") {
            pathname = "/apps";
        }
        return this.context.router.replaceWith(pathname);
    }

    render() {
        return null;
    }
}

MatchStartRoute.contextTypes = {
    router: React.PropTypes.object,
    currentUser: React.PropTypes.object
};

export default MatchStartRoute;
