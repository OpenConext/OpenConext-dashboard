import React from "react";
import I18n from "i18n-js";
import ReactTooltip from "react-tooltip";

class ServiceFilter extends React.Component {

    render() {
        const {filters, onChange, search, searchChange} = this.props;

        return (
            <div className="mod-filters">
                <div className="header">
                    <h1>{I18n.t("service_filter.title")}</h1>
                </div>
                <fieldset>
                    <input
                        type="search"
                        value={search}
                        onChange={searchChange}
                        placeholder={I18n.t("service_filter.search")}/>
                </fieldset>
                <section>
                    {Object.keys(filters).map(key => this.renderFilter(key, filters[key], onChange))}
                </section>
            </div>
        );
    }

    renderFilter(key, filter, onChange) {
        return (
            <fieldset key={filter.name}>
                <h2>{filter.name}{filter.tooltip && <span>
                            <i className="fa fa-info-circle" data-for={filter.name} data-tip></i>
                                <ReactTooltip id={filter.name} type="info" class="tool-tip" effect="solid"
                                              multiline={true}>
                                    <span dangerouslySetInnerHTML={{__html: filter.tooltip}}/>
                                </ReactTooltip>
                        </span>}</h2>
                {filter.values.map((value, index) => {
                    return this.renderFilterValue(key, value, index, onChange);
                })}
            </fieldset>
        );
    }

    renderFilterValue(key, filterValue, index, onChange) {
        return (
            <label key={filterValue.name} className={filterValue.count === 0 ? "greyed-out" : ""}>
                <input
                    checked={filterValue.checked}
                    type="checkbox"
                    onChange={onChange(key, index)}/>
                {filterValue.name} ({filterValue.count})
            </label>
        );
    }

}

ServiceFilter.propTypes = {
    onChange: React.PropTypes.func.isRequired,
    filters: React.PropTypes.object,
    search: React.PropTypes.string,
    searchChange: React.PropTypes.func
};

export default ServiceFilter;
