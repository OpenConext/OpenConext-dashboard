import React from "react";
import PropTypes from "prop-types";
import * as HighChart from "highcharts";
import * as HighStock from "highcharts/highstock";
import HighChartContainer from "./high_chart_container";
import I18n from "i18n-js";
import moment from "moment";
import Exporter from 'highcharts/modules/exporting';
import ExportData from 'highcharts/modules/export-data';
import {getDateTimeFormat} from "../utils/time";
import "moment/locale/nl";
import "moment/locale/pt";
import download from "downloadjs-next";

Exporter(HighChart);
Exporter(HighStock);
ExportData(HighChart);
ExportData(HighStock);

moment.locale(I18n.locale);
const navigation = () => ({
    buttonOptions: {
        symbolSize: 18,
        symbolStrokeWidth: 4
    }
});
const exporting = () => ({
    enabled: true,
    allowHTML: true,
    filename: "SURFconext logins",
    buttons: {
        contextButton: {
            symbolStroke: '#4DB2CF',
            menuItems: [
                {
                    text: I18n.t("export.downloadCSV"),
                    onclick: function () {
                        const csv = this.getCSV();
                        const cleanedCsv = csv.replace(/"<span[^>]+(.*?)<\/span>"/g, "$1").replace(/>/g, "");
                        download("data:text/csv,\ufeff" + encodeURIComponent(cleanedCsv), "stats", "text/csv")
                        // var a = this;
                        // debugger;
                        // this.fileDownload("data:text/csv,\ufeff" + encodeURIComponent(cleanedCsv), "csv", cleanedCsv, "text/csv")
                    }
                },
                'separator',
                'downloadPNG',
                'downloadPDF',
            ]
        },
    },
});

export default class Chart extends React.PureComponent {

    nonAggregatedOptions = (data, includeUniques, guest, scale) => {
        const series = [{
            color: "#D4AF37",
            name: I18n.t("chart.userCount"),
            data: data.filter(p => p.count_user_id !== undefined).map(p => [p.time, p.count_user_id])
        }];
        if (includeUniques) {
            series.push({
                color: "#15A300",
                name: I18n.t("chart.uniqueUserCount"),
                data: data.filter(p => p.distinct_count_user_id !== undefined).map(p => [p.time, p.distinct_count_user_id])
            });
        }
        return {
            chart: {
                zoomType: "x",
                height: guest ? 525 : 682,
                type: scale === "minute" ? "line" : "column"
            },
            title: {text: null},
            yAxis: {
                title: {text: scale === "all" ? I18n.t("chart.chartAll") : I18n.t("chart.chart", {scale: scale})},
                labels: {},
                min: 0,
                offset: 35,
                allowDecimals: false,
                plotLines: [{
                    value: 0,
                    width: 2,
                    color: "silver"
                }]
            },
            tooltip: {
                formatter: function () {
                    let res = this.points.reduce((acc, point) => {
                        acc += `
<div style="display: flex;align-items: center; margin-bottom: 5px">
    <span style="color:${point.color};font-size:16px;margin-right: 5px;display: inline-block">\u25CF</span>
    <span style="margin-right: 5px">${point.series.name}:</span>
    <span style="margin-left:auto;display: inline-block; font-weight:bold">${(point.y).toLocaleString()}</span>
</div>`;
                        return acc
                    }, "");
                    let m = moment.unix(this.x / 1000);
                    let dtf = "LLL";
                    if (scale !== "minute" && scale !== "hour") {
                        m = m.utc();
                        dtf = getDateTimeFormat(scale);
                    }
                    res += `<em style="font-size: 11px;margin-left: 3px;display: inline-block">${m.format(dtf)}</em>`;
                    return res;
                },
                useHTML: true,
                shared: true,
                backgroundColor: "rgba(255,255,255,1)"
            },
            xAxis: {
                type: "datetime",
                labels: {
                    formatter: function () {
                        if (series[0].data.length === 1) {
                            let m = moment(this.value);
                            if (scale !== "minute" && scale !== "hour") {
                                m = m.utc();
                            }
                            return m.format(getDateTimeFormat(scale));
                        } else {
                            return this.axis.defaultLabelFormatter.call(this)
                        }
                    }
                },
            },
            legend: {verticalAlign: "top"},
            rangeSelector: {
                buttons: [],
                enabled: false
            },
            navigation: navigation(),
            exporting: exporting(),
            credits: {enabled: false},
            plotOptions: {
                series: {
                    column: {
                        pointPadding: 0,
                        borderWidth: 0,
                        groupPadding: 0,
                    },
                    dataGrouping: {
                        enabled: data.length > 74880
                    },
                    showInNavigator: true,
                    marker: {
                        enabled: true,
                        radius: data.length < 3 ? 12 : data.length > 31 ? 0 : 5
                    },
                    lineWidth: 3,
                    states: {
                        hover: {
                            lineWidth: 1
                        }
                    },
                    threshold: null,
                }
            },
            series: series
        }
    };

    aggregatedOptions = (data, yValues, includeUniques, guest) => {
        const series = [
            {name: I18n.t("chart.userCount"), color: "#D4AF37", data: data.map(p => p.count_user_id)}
        ];

        if (includeUniques) {
            series.push({
                name: I18n.t("chart.uniqueUserCount"),
                color: "#15A300",
                data: data.map(p => p.distinct_count_user_id)
            })
        }
        return {
            chart: {
                type: "bar",
                height: Math.max(data.length * 50 + 120, guest ? 575 : 350)
            },
            title: {text: null},
            xAxis: {
                categories: yValues, title: {text: null},
                labels: {
                    useHTML: true
                }
            },
            yAxis: {min: 0, allowDecimals: false, title: {text: null}},
            tooltip: {
                useHTML: false,
                shared: true,
                backgroundColor: "rgba(255,255,255,1)",
                positioner: function (labelWidth, labelHeight, point) {
                    return {x: this.chart.axisOffset[3] + 15, y: point.plotY};
                }
            },
            plotOptions: {bar: {dataLabels: {enabled: true}}},
            legend: {verticalAlign: "top"},
            navigation: navigation(),
            exporting: exporting(),
            credits: {enabled: false},
            series: series,
        };
    };

    renderYvalue = (point, identityProvidersDict, serviceProvidersDict) => {
        const sp = serviceProvidersDict[point.sp_entity_id];
        return sp || point.sp_entity_id;
    };

    renderChart = (data, includeUniques, title, aggregate, groupedByIdp, groupedBySp, identityProvidersDict,
                   serviceProvidersDict, guest, scale) => {
        const userCount = data.filter(p => p.sp_entity_id);
        const yValues = aggregate ? userCount.map(p => this.renderYvalue(p, identityProvidersDict, serviceProvidersDict)) : [];

        const options = aggregate ? this.aggregatedOptions(data, yValues, includeUniques, guest) :
            this.nonAggregatedOptions(data, includeUniques, guest, scale);
        const rightClassName = this.props.rightDisabled ? "disabled" : "";
        return (
            <section className="chart">
                {title && <span className="title">{title}</span>}
                <HighChartContainer highcharts={aggregate ? HighChart : HighStock}
                                    constructorType={aggregate ? "chart" : "stockChart"}
                                    options={options}/>
                {(!aggregate && !this.props.noTimeFrame) && <section className="navigate">
                    <span onClick={this.props.goLeft}><i className="fa fa-arrow-left"></i></span>
                    <span onClick={this.props.goRight}><i className={`fa fa-arrow-right ${rightClassName}`}></i></span>
                </section>}
            </section>
        );
    };

    render() {
        const {
            data, scale, includeUniques, title, aggregate, groupedBySp, groupedByIdp, identityProvidersDict,
            serviceProvidersDict, guest
        } = this.props;
        return <div className="chart-container">
            {this.renderChart(data, includeUniques, title, aggregate, groupedByIdp, groupedBySp, identityProvidersDict,
                serviceProvidersDict, guest, scale)}
        </div>
    };
}
Chart.propTypes = {
    data: PropTypes.array.isRequired,
    scale: PropTypes.string.isRequired,
    includeUniques: PropTypes.bool,
    title: PropTypes.string,
    groupedBySp: PropTypes.bool,
    groupedByIdp: PropTypes.bool,
    aggregate: PropTypes.bool,
    serviceProvidersDict: PropTypes.object.isRequired,
    identityProvidersDict: PropTypes.object.isRequired,
    guest: PropTypes.bool,
    goLeft: PropTypes.func,
    goRight: PropTypes.func,
    rightDisabled: PropTypes.bool,
    noTimeFrame: PropTypes.bool
};
