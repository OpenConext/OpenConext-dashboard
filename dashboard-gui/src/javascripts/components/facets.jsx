import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import {FacetShape} from "../shapes";
import ReactTooltip from "react-tooltip";
import stopEvent from "../utils/stop";

class Facets extends React.Component {
    render() {
        const {facets} = this.props;

        return (
            <div className="mod-filters">
                <div className="header">
                    <h1>{I18n.t("facets.title")}</h1>
                </div>
                <form>
                    <fieldset>
                        {this.renderTotals()}
                    </fieldset>
                    <fieldset>
                        {this.renderResetFilters()}
                        {this.renderDownloadButton()}
                    </fieldset>
                    {facets.map(facet => this.renderFacet(facet))}
                    <fieldset>
                        {this.renderTotals()}
                    </fieldset>
                </form>
            </div>
        );
    }

    renderResetFilters() {
        return (
            <a
                className={"c-button" + (this.props.filteredCount >= this.props.totalCount ? " disabled" : "")}
                href="/reset"
                onClick={this.handleResetFilters.bind(this)}>{I18n.t("facets.reset")}</a>
        );
    }

    renderTotals() {
        const count = this.props.filteredCount;
        const total = this.props.totalCount;

        if (count === total) {
            return I18n.t("facets.totals.all", {total: total});
        }

        return I18n.t("facets.totals.filtered", {count: count, total: total});
    }

    renderFacet(facet) {
        return (
            <fieldset key={facet.name}>
                <a href="/dropdown" onClick={this.handleFacetToggle(facet)}>
                    {this.renderDropDownIndicator(facet)}
                </a>

                <span>{facet.name}{facet.tooltip && <span>
                            <i className="fa fa-info-circle" data-for={facet.name} data-tip></i>
                                <ReactTooltip id={facet.name} type="info" class="tool-tip" effect="solid"
                                              multiline={true} delayHide={250}>
                                    <span dangerouslySetInnerHTML={{__html: facet.tooltip}}/>
                                </ReactTooltip>
                        </span>}</span>
                {this.renderFacetOptions(facet)}
            </fieldset>
        );
    }

    renderFacetOptions(facet) {
        if (!this.props.hiddenFacets[facet.name]) {
            return (
                facet.values.map(value => {
                    return this.renderFacetValue(facet, value);
                }));
        }
        return null;
    }

    handleFacetToggle(facet) {
        return function (e) {
            stopEvent(e);
            this.props.onHide(facet);
        }.bind(this);
    }

    renderDropDownIndicator(facet) {
        if (this.props.hiddenFacets[facet.name]) {
            return <i className="fa fa-caret-down"/>;
        }

        return <i className="fa fa-caret-up"/>;
    }

    renderFacetValue(facet, facetValue) {
        const facetName = facet.searchValue || facet.name;
        const value = facetValue.searchValue || facetValue.value;
        const facetValueLabel = facetValue.value === "1.3.6.1.4.1.1076.20.40.40.1" ? "CollabPersonId" : facetValue.value;
        const checked = Array.isArray(this.props.selectedFacets[facetName]) && this.props.selectedFacets[facetName].indexOf(value) > -1;
        return (
            <label key={facetValue.value} className={facetValue.count === 0 ? "greyed-out" : ""}>
                <input className={checked ? "checked" : "unchecked"}
                       checked={checked}
                       type="checkbox"
                       onChange={this.handleSelectFacet(facetName, value)}/>
                {facetValueLabel} ({facetValue.count})
            </label>
        );
    }

    renderDownloadButton() {
        if ("msSaveBlob" in window.navigator || "download" in HTMLAnchorElement.prototype) {
            const {downloading} = this.props;
            const className = (this.props.filteredCount <= 0 || downloading ? " disabled" : "");
            return <span onClick={this.handleDownload.bind(this)}
                      className={"download-button c-button" + className}>{I18n.t("facets.download")}</span>;
        }
        return null;
    }

    handleDownload(e) {
        stopEvent(e);
        this.props.onDownload();
    }

    handleSelectFacet(facet, facetValue) {
        return function (e) {
            // stopEvent(e);
            this.props.onChange(facet, facetValue, e.target.checked);
        }.bind(this);
    }

    handleResetFilters(e) {
        stopEvent(e);
        this.props.onReset();
    }

}

Facets.propTypes = {
    onReset: PropTypes.func.isRequired,
    onDownload: PropTypes.func.isRequired,
    onChange: PropTypes.func.isRequired,
    onHide: PropTypes.func.isRequired,
    facets: PropTypes.arrayOf(FacetShape),
    filteredCount: PropTypes.number.isRequired,
    totalCount: PropTypes.number.isRequired,
    hiddenFacets: PropTypes.objectOf(PropTypes.bool),
    selectedFacets: PropTypes.objectOf(PropTypes.arrayOf(PropTypes.string)),
    download: PropTypes.bool.isRequired,
    downloading: PropTypes.bool.isRequired
};

export default Facets;
