import React from "react";
import {loginAggregated, loginTimeFrame, uniqueLoginCount} from "../api";
import I18n from "i18n-js";
import "./Live.css";
import Period from "../components/Period";
import moment from "moment";
import Chart from "../components/Chart";
import PropTypes from "prop-types";
import GroupBy from "../components/GroupBy";
import {isEmpty, stop} from "../utils/Utils";
import {getPeriod} from "../utils/Time";
import "moment/locale/nl";
import Filters from "../components/Filters";
import SelectPeriod from "../components/SelectPeriod";

moment.locale(I18n.locale);

export default class Live extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            data: [],
            from: moment().subtract(24, "hour"),
            to: moment().add(1, "day").startOf("day"),
            scale: "minute",
            sp: undefined,
            idp: undefined,
            institutionType: undefined,
            groupedByIdp: false,
            groupedBySp: false,
            includeUniques: !this.props.user.guest,
            providerState: "all",
            download: false,
            matrix: [],
            maximumTo: false,
            noTimeFrame: false,
        };
    }

    componentDidMount() {
        this.setState({data: []}, this.refreshStats);
    }

    refreshStats() {
        const {from, to, scale, idp, sp, groupedBySp, groupedByIdp, includeUniques, providerState, noTimeFrame} =
            this.state;
        let groupBy = undefined;
        if (isEmpty(sp) || isEmpty(idp)) {
            groupBy = `${groupedByIdp ? "idp_id" : ""},${groupedBySp ? "sp_id" : ""}`
        }
        const period = getPeriod(from, scale);
        if (noTimeFrame) {
            uniqueLoginCount({
                from: from ? from.unix() : moment().startOf("day").unix(),
                to: to ? to.unix() : moment().endOf("day").unix(),
                idp_id: idp,
                sp_id: sp,
                epoch: "ms",
                state: providerState
            }).then(res => this.setState({data: res}));
        }
        else if (groupedBySp || groupedByIdp) {
            this.doAggregatedLogin(period, includeUniques, from, to, idp, sp, groupBy, providerState, groupedBySp, groupedByIdp);
        } else {
            loginTimeFrame({
                from: from ? from.unix() : moment().startOf("day").unix(),
                to: to ? to.unix() : moment().endOf("day").unix(),
                include_unique: includeUniques,
                scale: scale,
                idp_id: idp,
                sp_id: sp,
                epoch: "ms",
                state: providerState
            }).then(res => {
                if (scale === "minute" || scale === "hour") {
                    res = res.filter(p => p.count_user_id > 0);
                    res = res.slice(1, res.length - 1);
                }
                this.setState({data: res});
            });
        }
    }

    doAggregatedLogin = (period, includeUniques, from, to, idp, sp, groupBy, providerState, groupedBySp, groupedByIdp, download = false) => {
        loginAggregated({
            period: period,
            include_unique: includeUniques,
            from: period ? undefined : from.unix(),
            to: period ? undefined : to ? to.unix() : undefined,
            idp_id: idp,
            sp_id: sp,
            group_by: groupBy,
            state: providerState
        }).then(res => {
            if (isEmpty(res)) {
                if (download) {
                    this.setState({matrix: res, download: true}, () => this.setState({download: false}));
                } else {
                    this.setState({data: res});
                }
            } else if (res.length === 1 && res[0] === "no_results") {
                this.setState({data: res});
            } else if (groupedBySp || groupedByIdp) {
                const sorted = res.filter(p => p.count_user_id).sort((a, b) => b.count_user_id - a.count_user_id);
                const uniqueOnes = res.filter(p => p.distinct_count_user_id).reduce((acc, p) => {
                    const key = (groupedByIdp && groupedBySp) ? `${p.sp_entity_id}${p.idp_entity_id}` : groupedBySp ? p.sp_entity_id : p.idp_entity_id;
                    acc[key] = p.distinct_count_user_id;
                    return acc;
                }, {});
                const data = sorted.map(p => {
                    const key = (groupedByIdp && groupedBySp) ? `${p.sp_entity_id}${p.idp_entity_id}` : groupedBySp ? p.sp_entity_id : p.idp_entity_id;
                    p.distinct_count_user_id = uniqueOnes[key] || 0;
                    return p;
                });
                if (download) {
                    this.setState({matrix: data, download: true}, this.setState({download: false}));
                } else {
                    this.setState({data: data});
                }
            } else {
                this.setState({
                    data: [{
                        count_user_id: (res.filter(p => p.count_user_id)[0] || {}).count_user_id || 0,
                        distinct_count_user_id: (res.filter(p => p.distinct_count_user_id)[0] || {}).distinct_count_user_id || 0,
                        time: res[0].time
                    }]
                })
            }
        });
    };

    goLeft = e => {
        stop(e);
        const scale = this.state.scale === "minute" || this.state.scale === "hour" ? "day" : this.state.scale;
        const from = moment(this.state.from).add(-1, scale);
        const to = moment(this.state.to).add(-1, scale);
        this.setState({from: from, to: to, maximumTo: false}, this.componentDidMount)
    };

    goRight = e => {
        stop(e);
        if (this.state.maximumTo) {
            return;
        }
        const scale = this.state.scale === "minute" || this.state.scale === "hour" ? "day" : this.state.scale;
        const from = moment(this.state.from).add(1, scale);
        const to = moment(this.state.to).add(1, scale);
        const tomorrowMidnight = moment().add(1, "day").startOf("day");
        const maximumTo = tomorrowMidnight.isBefore(to);
        this.setState({
            from: from,
            to: maximumTo ? tomorrowMidnight : to,
            maximumTo: maximumTo
        }, this.componentDidMount);
    };

    onChangeFrom = val => {
        const {scale, to} = this.state;
        let additionalState = {};
        const diff = moment.duration(to.diff(val)).asDays();
        if (scale === "minute" && diff > 1) {
            additionalState = {to: moment(val).add(1, "day"), includeUniques: false};
        } else if (scale === "hour" && diff > 7) {
            additionalState = {to: moment(val).add(7, "day"), includeUniques: false};
        }
        this.setState({data: [], from: val, ...additionalState}, this.componentDidMount)
    };

    onChangeTo = val => {
        const {scale, from} = this.state;
        let additionalState = {};
        const diff = moment.duration(val.diff(from)).asDays();
        if (scale === "minute" && diff > 1) {
            additionalState = {from: moment(val).add(-1, "day"), includeUniques: false};
        } else if (scale === "hour" && diff > 7) {
            additionalState = {from: moment(val).add(-7, "day"), includeUniques: false};
        }
        const tomorrowMidnight = moment().add(1, "day").startOf("day");
        const maximumTo = tomorrowMidnight.isBefore(val);
        this.setState({data: [], maximumTo: maximumTo, to: val, ...additionalState}, this.componentDidMount)
    };

    onChangeSp = val => this.setState({data: [], sp: val},
        this.componentDidMount);

    onChangeInstitutionType = val => this.setState({institutionType: val});

    onChangeState = val => this.setState({data: [], providerState: val}, this.componentDidMount);

    onChangeIdP = val => this.setState({data: [], idp: val},
        this.componentDidMount);

    onChangeScale = scale => {
        const {from, to} = this.state;
        let additionalState = {};
        const diff = moment.duration(to.diff(from)).asDays();
        if (scale === "minute" && diff > 1) {
            additionalState = {to: moment(from).add(1, "day"), includeUniques: false};
        } else if (scale === "hour" && diff > 7) {
            additionalState = {to: moment(from).add(7, "day"), includeUniques: false};
        }
        const state = {data: [], scale: scale, ...additionalState};
        this.setState(state, this.componentDidMount);
    };

    onChangeSelectPeriod = newState => this.setState(newState, this.componentDidMount);

    onChangeUniques = e => this.setState({data: [], includeUniques: e.target.checked}, this.componentDidMount);

    onChangeGroupBySp = e => this.setState({
        data: [], groupedBySp: e.target.checked,
        groupedByIdp: false,
        scale: this.state.scale === "minute" || this.state.scale === "hour" ? "month" : this.state.scale,
        institutionType: "",
    }, this.componentDidMount);

    onChangeGroupByIdp = e => this.setState({
        data: [], groupedByIdp: e.target.checked,
        groupedBySp: false,
        institutionType: "",
        scale: this.state.scale === "minute" || this.state.scale === "hour" ? "month" : this.state.scale
    }, this.componentDidMount);

    onChangeNoTimeFrame = () => this.setState({
        noTimeFrame: !this.state.noTimeFrame
    }, this.componentDidMount);

    onLabelClick = entityId => {
        const {groupedByIdp, groupedBySp} = this.state;
        this.setState({
            sp: groupedBySp ? entityId : "", idp: groupedByIdp ? entityId : "",
            groupedByIdp: !groupedByIdp, groupedBySp: !groupedBySp
        }, this.componentDidMount)
    };

    onDownload = e => {
        stop(e);
        const {from, scale, providerState} = this.state;
        const period = getPeriod(from, scale === "minute" || scale === "hour" ? "year" : scale || "year");
        this.doAggregatedLogin(period, true, undefined, undefined, undefined, undefined, "idp_id,sp_id", providerState, true, true, undefined, true)
    };

    title = (from, to, aggregate, groupedBySp, groupedByIdp, scale, noTimeFrame) => {
        const format = scale === "minute" || scale === "hour" ? "L" : "L";//'MMMM Do YYYY, h:mm:ss a'
        if (noTimeFrame) {
            return I18n.t("live.noTimeFrameChart", {
                from: from ? from.format(format) : "",
                to: to ? to.format(format) : "",
                scale: I18n.t(`period.${scale}`).toLowerCase()
            });
        }
        if (!aggregate) {
            return I18n.t("live.chartTitle", {
                from: from ? from.format(format) : "",
                to: to ? to.format(format) : "",
                scale: I18n.t(`period.${scale}`).toLowerCase()
            });
        }
        if (aggregate) {
            return I18n.t("live.aggregatedChartTitlePeriod", {
                period: getPeriod(from, scale),
                group: I18n.t(`providers.${groupedByIdp ? "idp" : "sp"}`)
            });
        }
    };

    render() {
        const {
            data, from, to, scale, sp, idp, groupedByIdp, groupedBySp, providerState, includeUniques, download,
            matrix, institutionType, maximumTo, noTimeFrame
        } = this.state;
        const aggregate = groupedByIdp || groupedBySp;
        const {identityProviders, serviceProviders, user, identityProvidersDict, serviceProvidersDict} = this.props;
        let dataForChart = data;
        if (!isEmpty(institutionType)) {
            const entityIds = identityProviders.filter(idp => idp["coin:institution_type"] === institutionType).map(idp => idp.id);
            dataForChart = data.filter(idp => entityIds.indexOf(idp["idp_entity_id"]) > -1);
        }
        const disabled = [];
        if (isEmpty(sp) || isEmpty(idp) || groupedByIdp || groupedBySp) {
            disabled.push("noTimeframe");
        }
        if (noTimeFrame) {
            disabled.push("scale");
        }
        return (
            <div className={`live ${user.guest ? "guest" : ""}`}>
                <section className={`container ${user.guest ? "guest" : ""}`}>
                    {user.guest && <SelectPeriod onChangeSelectPeriod={this.onChangeSelectPeriod}/>}
                    {!user.guest && <Period onChangeFrom={this.onChangeFrom}
                                            onChangeTo={this.onChangeTo}
                                            onChangeScale={this.onChangeScale}
                                            from={from}
                                            to={to}
                                            scale={scale}
                                            aggregate={aggregate}
                                            noTimeFrame={noTimeFrame}
                                            disabled={disabled}
                                            changeTimeFrame={this.onChangeNoTimeFrame}/>}
                    {!user.guest && <GroupBy groupedByIdp={groupedByIdp}
                                             groupedBySp={groupedBySp}
                                             onChangeGroupByIdp={this.onChangeGroupByIdp}
                                             onChangeGroupBySp={this.onChangeGroupBySp}
                                             download={download}
                                             onDownload={this.onDownload}
                                             matrix={matrix}/>}
                    {!user.guest && <Filters onChangeState={this.onChangeState}
                                             onChangeUniques={this.onChangeUniques}
                                             state={providerState}
                                             uniques={includeUniques}
                                             displayUniques={scale !== "minute" && scale !== "hour"}
                                             onChangeSp={this.onChangeSp}
                                             onChangeIdp={this.onChangeIdP}
                                             identityProviders={identityProviders}
                                             serviceProviders={serviceProviders}
                                             idp={idp}
                                             sp={sp}
                                             onChangeInstitutionType={this.onChangeInstitutionType}
                                             institutionType={institutionType}
                                             groupedByIdp={groupedByIdp}/>}
                </section>
                <Chart data={dataForChart}
                       scale={scale}
                       includeUniques={includeUniques}
                       title={this.title(from, to, aggregate, groupedBySp, groupedByIdp, scale, noTimeFrame)}
                       groupedBySp={groupedBySp}
                       groupedByIdp={groupedByIdp}
                       aggregate={aggregate}
                       identityProvidersDict={identityProvidersDict}
                       serviceProvidersDict={serviceProvidersDict}
                       guest={user.guest}
                       goRight={this.goRight}
                       goLeft={this.goLeft}
                       rightDisabled={maximumTo}
                       onLabelClick={this.onLabelClick}
                       noTimeFrame={noTimeFrame}/>
            </div>
        );
    }
}

Live.propTypes = {
    identityProviders: PropTypes.array.isRequired,
    serviceProviders: PropTypes.array.isRequired,
    serviceProvidersDict: PropTypes.object.isRequired,
    identityProvidersDict: PropTypes.object.isRequired,
    user: PropTypes.object.isRequired
};