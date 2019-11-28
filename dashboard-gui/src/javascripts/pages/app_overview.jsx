import React from "react";
import ReactDOM from "react-dom";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import SortableHeader from "../components/sortable_header";
import {Link} from "react-router-dom";
import {disableConsent, exportApps, getApps} from "../api";
import sort from "../utils/sort";
import pagination from "../utils/pagination";
import Facets from "../components/facets";
import YesNo from "../components/yes_no";
import merge from "lodash.merge";
import isUndefined from "lodash.isundefined";
import pickBy from "lodash.pickby";
import isEmpty from "lodash.isempty";
import includes from "lodash.includes";
import stopEvent from "../utils/stop";
import {consentTypes} from "../utils/utils";
import spinner from "../lib/spin";

const store = {
    activeFacets: null,
    hiddenFacets: null,
    page: null,
    appId: null,
    query: null
};

const pageCount = 20;

class AppOverview extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            apps: [],
            facets: [],
            arpAttributes: [],
            search: store.query || "",
            activeFacets: store.activeFacets || {},
            hiddenFacets: store.hiddenFacets || {},
            sortAttribute: "name",
            sortAscending: undefined,
            page: store.page || 1,
            download: false,
            downloading: false,
            exportResult: [],
            idpDisableConsent: [],
            loading: true
        };
        if (store.appId) {
            this.appRef = React.createRef();
        }
    }

    componentDidMount() {
        spinner.start();
        spinner.ignore = true;
        this.setState({loading: true});
        const back = this.props.match.params.back;
        if (isEmpty(back)) {
            Object.keys(store).forEach(k => store[k] = null);
        }
        Promise.all([getApps(), disableConsent()])
            .then(data => {
                let {facets, apps: unfilteredApps} = data[0].payload;
                const {currentUser} = this.context;
                const idpState = currentUser.getCurrentIdp().state;

                const apps = unfilteredApps
                    .filter(app => app.state === idpState)
                    .filter(app => !(currentUser.guest && app.idpVisibleOnly));
                // We need to sanitize the categories data for each app to ensure the facet totals are correct
                const unknown = {value: I18n.t("facets.unknown")};
                facets.forEach(facet => {
                    apps.forEach(app => {
                        app.categories = app.categories || [];
                        const appCategory = app.categories.filter(category => category.name === facet.name);
                        if (appCategory.length === 0) {
                            app.categories.push({name: facet.name, values: [unknown]});
                            const filtered = facet.values.filter(facetValue => {
                                return facetValue.value === unknown.value;
                            });
                            if (!filtered[0]) {
                                facet.values.push(Object.assign({}, unknown));
                            }
                        }
                    });
                });
                const attributes = apps.reduce((acc, app) => {
                    Object.keys(app.arp.attributes).forEach(attr => {
                        if (acc.indexOf(attr) < 0) {
                            acc.push(attr)
                        }
                    });
                    return acc;
                }, []);
                const initialHiddenFacets = {};
                initialHiddenFacets[I18n.t("facets.static.arp.name")] = true;
                const back = this.props.match.params.back;
                const page = back && store.page ? store.page : 1;
                const search = back && store.query ? store.query : "";
                this.setState({
                    apps: apps,
                    idpDisableConsent: data[1],
                    facets: facets,
                    arpAttributes: [...attributes],
                    hiddenFacets: initialHiddenFacets,
                    page: page,
                    search: search,
                    loading: false
                }, () => setTimeout(() => {
                    const back = this.props.match.params.back;
                    if (back && this.appRef && this.appRef.current) {
                        const appNode = ReactDOM.findDOMNode(this.appRef.current);
                        if (appNode) {
                            this.scrollToPos(0, appNode.offsetTop);
                        }
                    }
                }, 350));
                spinner.ignore = false;
                spinner.stop();
            });
    }

    handleSort(sortObject) {
        this.setState({
            sortAttribute: sortObject.sortAttribute,
            sortAscending: sortObject.sortAscending
        });
    }

    renderSortableHeader(className, attribute) {
        return (
            <SortableHeader
                sortAttribute={this.state.sortAttribute}
                attribute={attribute}
                sortAscending={this.state.sortAscending}
                localeKey="apps.overview"
                className={className}
                onSort={this.handleSort.bind(this)}
            />
        );
    }

    renderEmpty() {
        return (
            <tr>
                <td className="empty" colSpan="4">{I18n.t("apps.overview.no_results")}</td>
            </tr>
        );
    }

    renderProcessing() {
        return (
            <tr>
                <td className="processing_results" colSpan="4">{I18n.t("apps.overview.processing_results")}</td>
            </tr>
        );
    }

    renderApp(app, index) {
        const {currentUser} = this.context;
        const currentIdp = currentUser.getCurrentIdp();
        const connect = currentUser.dashboardAdmin && currentIdp.institutionId && currentIdp.state !== "testaccepted";
        const focus = app.id === store.appId && this.props.match.params.back;
        return (
            <tr key={index} ref={re => {
                if (re && app.id === store.appId) {
                    this.appRef.current = re;
                }
            }} onClick={e => this.handleShowAppDetail(e, app)}
                className={focus ? "focus" : ""}>
                <td><Link
                    to={`/apps/${app.id}/${app.entityType}/overview`}
                    onClick={e => this.handleShowAppDetail(e, app)}>{app.name}</Link>
                </td>
                {this.renderLicenseNeeded(app)}
                {/*<YesNo value={!app.aansluitovereenkomstRefused}/>*/}
                {!currentUser.guest && <YesNo value={app.connected}/>}
                <td className="right">
                    {connect && this.renderConnectButton(app)}
                </td>
            </tr>
        );
    }

    licenseStatusClassName(app) {
        switch (app.licenseStatus) {
            case "HAS_LICENSE_SURFMARKET":
            case "HAS_LICENSE_SP":
                return "yes";
            case "NO_LICENSE":
                return "no";
            default:
                return "";
        }
    }

    renderLicenseNeeded(app) {
        return (
            <td
                className={`${this.licenseStatusClassName(app)}`}>{I18n.t("facets.static.license." + app.licenseStatus.toLowerCase())}</td>
        );
    }

    renderConnectButton(app) {
        if (!app.connected) {
            return <Link
                to={`/apps/${app.id}/${app.entityType}/how_to_connect`}
                className="c-button narrow"
                onClick={e => this.handleConnectApp(e, app)}>{I18n.t("apps.overview.connect_button")}</Link>;
        }
        return null;
    }

    handleShowAppDetail(e, app) {
        stopEvent(e);
        store.appId = app.id;
        store.page = this.state.page;
        store.query = this.state.search;
        this.props.history.push(`/apps/${app.id}/${app.entityType}/overview`);
    }

    handleConnectApp(e, app) {
        stopEvent(e);
        this.props.history.replace(`/apps/${app.id}/${app.entityType}/how_to_connect`);
    }

    /*
     * this.state.activeFacets is a object with facet names and the values are arrays with all select values
     */
    handleFacetChange(facet, facetValue, checked) {
        const selectedFacets = merge({}, this.state.activeFacets);
        let facetValues = selectedFacets[facet];

        if (isUndefined(facetValues)) {
            facetValues = selectedFacets[facet] = [facetValue];
        } else {
            checked ? facetValues.push(facetValue) : facetValues.splice(facetValues.indexOf(facetValue), 1);
        }

        this.setState({activeFacets: selectedFacets, page: 1});

        store.activeFacets = selectedFacets;
        store.page = 1;
    }

    handleFacetHide(facet) {
        const hiddenFacets = merge({}, this.state.hiddenFacets);
        if (hiddenFacets[facet.name]) {
            delete hiddenFacets[facet.name];
        } else {
            hiddenFacets[facet.name] = true;
        }
        this.setState({hiddenFacets: hiddenFacets});
        store.hiddenFacets = hiddenFacets;
    }

    handleResetFilters() {
        this.setState({
            search: "",
            activeFacets: {},
            hiddenFacets: {},
            page: 1
        });

        store.activeFacets = null;
        store.hiddenFacets = null;
        store.page = 1;
    }

    fake_click = obj => {
        const ev = document.createEvent("MouseEvents");
        ev.initMouseEvent("click", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
        obj.dispatchEvent(ev);
    };

    handleDownloadOverview = e => {
        stopEvent(e);
        if (this.state.downloading) {
            return;
        }
        this.setState({downloading: true});
        const {currentUser} = this.context;
        const filteredApps = this.filterAppsForInclusiveFilters(this.filterAppsForExclusiveFilters(this.state.apps));
        const ids = filteredApps.map(app => app.id);
        exportApps(currentUser.getCurrentIdpId(), ids).then(res => {
            const urlObject = window.URL || window.webkitURL || window;
            const lines = res.reduce((acc, arr) => {
                acc.push(arr.join(","));
                return acc;
            }, []);
            const csvContent = lines.join("\n");
            const export_blob = new Blob([csvContent]);
            if ("msSaveBlob" in window.navigator) {
                window.navigator.msSaveBlob(export_blob, "services.csv");
            } else if ("download" in HTMLAnchorElement.prototype) {
                const save_link = document.createElementNS("http://www.w3.org/1999/xhtml", "a");
                save_link.href = urlObject.createObjectURL(export_blob);
                save_link.download = "services.csv";
                this.fake_click(save_link);
            }
            this.setState({download: true}, () => this.setState({download: false, downloading: false}));
        });
    };

    filterAppsForExclusiveFilters(apps) {
        const searchString = this.state.search.toLowerCase();
        return searchString.length > 0 ? apps.filter(this.filterBySearchQuery.bind(this)) : apps;
    }

    filterAppsForInclusiveFilters(apps) {
        let filteredApps = apps;

        if (!isEmpty(this.state.activeFacets)) {
            filteredApps = filteredApps.filter(this.filterByFacets(this.state.activeFacets));
            this.staticFacets().forEach(facetObject => {
                filteredApps = filteredApps.filter(facetObject.filterApp);
            });
        }

        return filteredApps;
    }

    addNumbers(filteredApps, facets) {
        const {currentUser} = this.context;
        const stepupEntities = currentUser.getCurrentIdp().stepupEntities || [];
        const me = this;
        const filter = function (facet, filterFunction) {
            const activeFacets = this.state.activeFacets;
            const activeFacetsWithoutCurrent = pickBy(activeFacets, (value, key) => {
                return key !== facet.searchValue;
            });
            let filteredWithoutCurrentFacetApps = filteredApps.filter(this.filterByFacets(activeFacetsWithoutCurrent));

            this.staticFacets().filter(facetObject => {
                return facetObject.searchValue !== facet.searchValue;
            }).forEach(facetObject => {
                filteredWithoutCurrentFacetApps = filteredWithoutCurrentFacetApps.filter(facetObject.filterApp);
            });

            facet.values.forEach(facetValue => {
                facetValue.count = filteredWithoutCurrentFacetApps.filter(app => {
                    return filterFunction(app, facetValue);
                }).length;
            });
        }.bind(this);

        facets.forEach(facet => {
            switch (facet.searchValue) {
                case "connection":
                    filter(facet, (app, facetValue) => {
                        return facetValue.searchValue === "yes" ? app.connected : !app.connected;
                    });
                    break;
                case "license":
                    filter(facet, (app, facetValue) => {
                        return app.licenseStatus === facetValue.searchValue;
                    });
                    break;
                case "interfed_source":
                    filter(facet, (app, facetValue) => {
                        return app.interfedSource === facetValue.searchValue;
                    });
                    break;
                case "entity_category":
                    filter(facet, (app, facetValue) => {
                        return app.entityCategories1 === facetValue.searchValue || app.entityCategories2 === facetValue.searchValue;
                    });
                    break;
                case "used_by_idp":
                    filter(facet, (app, facetValue) => {
                        const usedByIdp = currentUser.getCurrentIdp().institutionId === app.institutionId;
                        return facetValue.searchValue === "yes" ? usedByIdp : !usedByIdp;
                    });
                    break;
                case "published_edugain":
                    filter(facet, (app, facetValue) => {
                        const published = app.publishedInEdugain || false;
                        return facetValue.searchValue === "yes" ? published : !published;
                    });
                    break;
                case "strong_authentication":
                    filter(facet, (app, facetValue) => {
                        const spMatches = facetValue.searchValue === "SP_" + app.minimalLoaLevel;
                        const stepUpEntity = stepupEntities.find(e => e.name === app.spEntityId);
                        const idppMatches = stepUpEntity != null && facetValue.searchValue === "IDP_" + stepUpEntity.level;
                        if (facetValue.searchValue === "NONE") {
                            return isEmpty(app.minimalLoaLevel) && isEmpty(stepUpEntity);
                        }
                        return spMatches || idppMatches;
                    });
                    break;
                case "manipulation_notes":
                    filter(facet, (app, facetValue) => {
                        const hasManipulationNotes = isEmpty(app.manipulationNotes);
                        return facetValue.searchValue === "yes" ? !hasManipulationNotes : hasManipulationNotes;
                    });
                    break;
                case "attributes":
                    filter(facet, (app, facetValue) => {
                        if (app.arp.noArp && !app.manipulation) {
                            return true;
                        }
                        if (app.arp.noArp || app.arp.noAttrArp) {
                            return false;
                        }
                        const requiredAttributes = Object.keys(app.arp.attributes);
                        return requiredAttributes.indexOf(facetValue.searchValue) > -1;
                    });
                    break;
                case "type_consent":
                    filter(facet, (app, facetValue) => {
                        const idpDisableConsent = this.state.idpDisableConsent || [];
                        const consent = idpDisableConsent.find(dc => dc.spEntityId === app.spEntityId) || {type: "DEFAULT_CONSENT"};
                        return facetValue.searchValue === consent.type;
                    });
                    break;
                default:
                    filter(facet, (app, facetValue) => {
                        const categories = me.normalizeCategories(app);
                        const appTags = categories[facet.searchValue] || [];
                        return appTags.indexOf(facetValue.value) > -1;
                    });
            }
        });
    }

    filterBySearchQuery(app) {
        const searchString = this.state.search.toLowerCase();
        return Object.values(app.descriptions).concat(Object.values(app.names))
            .some(name => name.toLowerCase().indexOf(searchString) > -1) || app.spEntityId.toLowerCase().indexOf(searchString) > -1;
    }

    filterYesNoFacet(name, yes) {
        const values = this.state.activeFacets[name] || [];
        return values.length === 0
            || (yes && includes(values, "yes"))
            || (!yes && includes(values, "no"));
    }

    filterByFacets(facets) {
        return function (app) {
            const normalizedCategories = this.normalizeCategories(app);
            for (const facet in facets) {
                if (facets.hasOwnProperty(facet)) {
                    const facetValues = facets[facet] || [];
                    if (normalizedCategories[facet] && facetValues.length > 0) {
                        const hits = normalizedCategories[facet].filter(facetValue => {
                            return facetValues.indexOf(facetValue) > -1;
                        });
                        if (hits.length === 0) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }.bind(this);
    }

    normalizeCategories(app) {
        const normalizedCategories = {};
        app.categories.forEach(category => {
            normalizedCategories[category.searchValue] = category.values.map(categoryValue => {
                return categoryValue.value;
            });
        });
        return normalizedCategories;
    }

    staticFacets() {
        const {currentUser} = this.context;
        const stepupEntities = currentUser.getCurrentIdp().stepupEntities || [];
        const {arpAttributes} = this.state;

        let results = [{
            name: I18n.t("facets.static.connection.name"),
            searchValue: "connection",
            values: [
                {value: I18n.t("facets.static.connection.has_connection"), searchValue: "yes"},
                {value: I18n.t("facets.static.connection.no_connection"), searchValue: "no"},
            ],
            filterApp: function (app) {
                return this.filterYesNoFacet("connection", app.connected);
            }.bind(this),
        }, {
            name: I18n.t("facets.static.used_by_idp.name"),
            searchValue: "used_by_idp",
            values: [
                {value: I18n.t("facets.static.used_by_idp.yes"), searchValue: "yes"},
                {value: I18n.t("facets.static.used_by_idp.no"), searchValue: "no"},
            ],
            filterApp: function (app) {
                return this.filterYesNoFacet("used_by_idp", currentUser.getCurrentIdp().institutionId === app.institutionId);
            }.bind(this),
        }, {
            name: I18n.t("facets.static.interfed_source.name"),
            tooltip: I18n.t("facets.static.interfed_source.tooltip"),
            searchValue: "interfed_source",
            values: [
                {value: I18n.t("facets.static.interfed_source.surfconext"), searchValue: "SURFconext"},
                {value: I18n.t("facets.static.interfed_source.edugain"), searchValue: "eduGAIN"},
                {value: I18n.t("facets.static.interfed_source.entree"), searchValue: "Entree"},
            ],
            filterApp: function (app) {
                const sourceFacetValues = this.state.activeFacets["interfed_source"] || [];
                return sourceFacetValues.length === 0 || sourceFacetValues.indexOf(app.interfedSource) > -1;
            }.bind(this)
        }, {
            name: I18n.t("facets.static.entity_category.name"),
            tooltip: I18n.t("facets.static.entity_category.tooltip"),
            searchValue: "entity_category",
            values: [
                {
                    value: I18n.t("facets.static.entity_category.code_of_conduct"),
                    searchValue: "http://www.geant.net/uri/dataprotection-code-of-conduct/v1"
                },
                {
                    value: I18n.t("facets.static.entity_category.research_and_scholarship"),
                    searchValue: "http://refeds.org/category/research-and-scholarship"
                }
            ],
            filterApp: function (app) {
                const sourceFacetValues = this.state.activeFacets["entity_category"] || [];
                return sourceFacetValues.length === 0 || sourceFacetValues.indexOf(app.entityCategories1) > -1 || sourceFacetValues.indexOf(app.entityCategories2) > -1;
            }.bind(this)
        }, {
            name: I18n.t("facets.static.license.name"),
            searchValue: "license",
            values: [
                {value: I18n.t("facets.static.license.has_license_surfmarket"), searchValue: "HAS_LICENSE_SURFMARKET"},
                {value: I18n.t("facets.static.license.has_license_sp"), searchValue: "HAS_LICENSE_SP"},
                {value: I18n.t("facets.static.license.not_needed"), searchValue: "NOT_NEEDED"},
                {value: I18n.t("facets.static.license.unknown"), searchValue: "UNKNOWN"},
            ],
            filterApp: function (app) {
                const licenseFacetValues = this.state.activeFacets["license"] || [];
                return licenseFacetValues.length === 0 || licenseFacetValues.indexOf(app.licenseStatus) > -1;
            }.bind(this)
        }, {
            name: I18n.t("facets.static.strong_authentication.name"),
            tooltip: I18n.t("facets.static.strong_authentication.tooltip"),
            searchValue: "strong_authentication",
            values: currentUser.loaLevels.reduce((acc, loa) => {
                acc.push({value: "SP - " + loa.substring(loa.lastIndexOf("/") + 1), searchValue: "SP_" + loa});
                acc.push({value: "IDP - " + loa.substring(loa.lastIndexOf("/") + 1), searchValue: "IDP_" + loa});
                return acc;
            }, [])
                .sort((a, b) => a.value.localeCompare(b.value))
                .concat([{value: I18n.t("facets.static.strong_authentication.none"), searchValue: "NONE"}]),
            filterApp: function (app) {
                const strongAuthenticationFacetValues = this.state.activeFacets["strong_authentication"] || [];
                const minimalLoaLevel = "SP_" + app.minimalLoaLevel;
                const stepUpEntity = stepupEntities.find(e => e.name === app.spEntityId);
                const none = strongAuthenticationFacetValues.indexOf("NONE") > -1 && strongAuthenticationFacetValues.length === 1;
                return strongAuthenticationFacetValues.length === 0 ||
                    none ||
                    strongAuthenticationFacetValues.indexOf(minimalLoaLevel) > -1 ||
                    (stepUpEntity != null && strongAuthenticationFacetValues.indexOf("IDP_" + stepUpEntity.level) > -1);
            }.bind(this)
        }, {
            name: I18n.t("facets.static.arp.name"),
            tooltip: I18n.t("facets.static.arp.tooltip"),
            searchValue: "attributes",
            values: arpAttributes.map(attr => {
                const val = attr.substring(attr.lastIndexOf(":") + 1);
                return {value: val.charAt(0).toUpperCase() + val.slice(1), searchValue: attr};
            }),
            filterApp: function (app) {
                const attrFacetValues = this.state.activeFacets["attributes"] || [];
                const attributes = Object.keys(app.arp.attributes);
                if ((app.arp.noArp && !app.manipulation) || attrFacetValues.length === 0) {
                    return true;
                }
                if (app.arp.noAttrArp) {
                    return false;
                }
                return attrFacetValues.filter(attr => attributes.indexOf(attr) > -1).length === attrFacetValues.length;
            }.bind(this)
        }, {
            name: I18n.t("facets.static.type_consent.name"),
            tooltip: I18n.t("facets.static.type_consent.tooltip"),
            searchValue: "type_consent",
            values: consentTypes.map(t => ({
                searchValue: t,
                value: I18n.t(`facets.static.type_consent.${t.toLowerCase()}`)
            })),
            filterApp: function (app) {
                const consentFacetValues = this.state.activeFacets["type_consent"] || [];
                const idpDisableConsent = this.state.idpDisableConsent || [];
                const consent = idpDisableConsent.find(dc => dc.spEntityId === app.spEntityId) || {type: "DEFAULT_CONSENT"};
                return consentFacetValues.length === 0 || consentFacetValues.includes(consent.type);
            }.bind(this)
        }
        ];
        if (currentUser.superUser) {
            results.push({
                name: I18n.t("facets.static.attribute_manipulation.name"),
                searchValue: "manipulation_notes",
                values: [
                    {value: I18n.t("facets.static.attribute_manipulation.yes"), searchValue: "yes"},
                    {value: I18n.t("facets.static.attribute_manipulation.no"), searchValue: "no"}
                ],
                filterApp: function (app) {
                    return this.filterYesNoFacet("manipulation_notes", app.manipulationNotes);
                }.bind(this)
            });
        }
        if (!currentUser.manageConsentEnabled) {
            results = results.filter(facet => facet.name !== I18n.t("facets.static.type_consent.name"));
        }
        if (currentUser.guest) {
            results = results.filter(facet => facet.name !== I18n.t("facets.static.connection.name") &&
                facet.name !== I18n.t("facets.static.used_by_idp.name"));
        }
        return results;
    }

    changePage(nbr) {
        this.setState({page: nbr}, () => this.scrollToPos(0, 0));
    }

    scrollToPos = (left, top) => {
        const options = {
            "left": left,
            "top": top
        };
        if (navigator.userAgent.indexOf("Firefox") === -1) {
            options.behavior = "smooth";
        }
        window.scrollTo(options);
    };

    renderPagination(resultLength, page) {
        if (resultLength <= pageCount) {
            return null;
        }
        const nbrPages = Math.ceil(resultLength / pageCount);
        const rangeWithDots = pagination(page, nbrPages);
        return (
            <section className="pagination">
                <section className="container">
                    {(nbrPages > 1 && page !== 1) &&
                    <i className="fa fa-arrow-left" onClick={this.changePage.bind(this, page - 1)}></i>}
                    {rangeWithDots.map((nbr, index) =>
                        typeof (nbr) === "string" || nbr instanceof String ?
                            <span key={index} className="dots">{nbr}</span> :
                            nbr === page ?
                                <span className="current" key={index}>{nbr}</span> :
                                <span key={index} onClick={this.changePage.bind(this, nbr)}>{nbr}</span>
                    )}
                    {(nbrPages > 1 && page !== nbrPages) &&
                    <i className="fa fa-arrow-right" onClick={this.changePage.bind(this, page + 1)}></i>}
                </section>

            </section>);
    }

    render() {
        const {currentUser} = this.context;
        const {sortAttribute, sortAscending, page, download, downloading, exportResult, loading} = this.state;
        const filteredExclusiveApps = this.filterAppsForExclusiveFilters(this.state.apps);
        let connect = null;

        if (currentUser.dashboardAdmin && currentUser.getCurrentIdp().institutionId) {
            connect = (
                <th className="percent_10 right">
                    {I18n.t("apps.overview.connect")}
                </th>
            );
        }

        const facets = this.staticFacets().concat(this.state.facets);
        this.addNumbers(filteredExclusiveApps, facets);
        const filteredApps = this.filterAppsForInclusiveFilters(filteredExclusiveApps);
        let sortedApps = sort(filteredApps, sortAttribute, sortAscending);

        if (sortedApps.length > pageCount) {
            sortedApps = sortedApps.slice((page - 1) * pageCount, page * pageCount);
        }

        return (
            <div className="l-main">
                <div className="l-left">
                    <Facets
                        facets={facets}
                        selectedFacets={this.state.activeFacets}
                        hiddenFacets={this.state.hiddenFacets}
                        filteredCount={filteredApps.length}
                        totalCount={this.state.apps.length}
                        onChange={this.handleFacetChange.bind(this)}
                        onHide={this.handleFacetHide.bind(this)}
                        onReset={this.handleResetFilters.bind(this)}
                        onDownload={this.handleDownloadOverview.bind(this)}
                        download={download}
                        downloading={downloading}
                        exportResult={exportResult}
                    />
                </div>
                <div className="l-right">
                    <div className="mod-app-search">
                        <div>
                            <i className="fa fa-search"/>
                            <input
                                type="search"
                                value={this.state.search}
                                onChange={e => this.setState({page: 1, search: e.target.value})}
                                placeholder={I18n.t("apps.overview.search_hint")}/>
                            <button type="submit">{I18n.t("apps.overview.search")}</button>
                        </div>
                    </div>

                    <div className="mod-app-list">
                        <table>
                            <thead>
                            <tr>
                                {this.renderSortableHeader(currentUser.guest ? "percent_60" : "percent_50", "name")}
                                {this.renderSortableHeader(currentUser.guest ? "percent_40" : "percent_30", "licenseStatus")}
                                {/*{this.renderSortableHeader(currentUser.guest ? "percent_30 bool" :"percent_20 bool", "aansluitovereenkomstRefused")}*/}
                                {!currentUser.guest && this.renderSortableHeader("percent_20 bool", "connected")}
                                {connect}
                            </tr>
                            </thead>
                            <tbody>
                            {sortedApps.length > 0 ? sortedApps.map((app, index) => this.renderApp(app, index)) :
                                (sortedApps.length === this.state.apps) || loading ? this.renderProcessing() : this.renderEmpty()}
                            </tbody>
                        </table>
                        {this.renderPagination(filteredApps.length, page)}
                    </div>

                    <div className="mod-app-list">
                        <table>
                            <tbody>
                            <tr>
                                <td dangerouslySetInnerHTML={{__html: I18n.t("apps.overview.add_services_hint")}}/>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        );
    }


}

AppOverview.contextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object
};

export default AppOverview;
