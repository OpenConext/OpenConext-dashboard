import React from "react";

// import DateTimePicker from "react-widgets/lib/DateTimePicker";
// import momentLocalizer from "react-widgets/lib/localizers/moment";
//
// momentLocalizer(moment);

class Period extends React.Component {
    constructor() {
        super();

        this.state = {
            date: new Date()
        };
    }

    componentWillMount() {
        this.setState({date: this.props.initialDate.toDate()});
    }

    // getFormat() {
    //     switch (this.props.period) {
    //         case "d":
    //             return "YYYY-MM-DD";
    //         case "w":
    //             return "YYYY[w]w";
    //         case "m":
    //             return "MMMM";
    //         case "q":
    //             return "YYYY[q]Q";
    //         case "y":
    //             return "YYYY";
    //         default:
    //             return "YYYY-MM-DD";
    //     }
    // }
    //
    // getInitialView() {
    //     switch (this.props.period) {
    //         case "y":
    //             return "decade";
    //         case "m":
    //         case "q":
    //             return "year";
    //         default:
    //             return "month";
    //     }
    // }
    //
    // render() {
    //     return (
    //         <fieldset>
    //             <h2>{this.props.title}</h2>
    //             <DateTimePicker
    //                 value={this.state.date}
    //                 onChange={e => this.handlePeriodDateChanged(e)}
    //                 time={false}
    //                 format={this.getFormat()}
    //                 initialView={this.getInitialView()}
    //                 max={this.props.max || moment().toDate()}
    //                 min={this.props.min}
    //             />
    //         </fieldset>
    //     );
    // }
    //
    // handlePeriodDateChanged(value) {
    //     if (value) {
    //         this.setState({date: value});
    //         const m = moment(value, "YYYY-MM-DD", true);
    //         if (m.isValid()) {
    //             this.props.handleChange(m);
    //         }
    //     }
    // }
}

// Period.defaultProps = {
//     period: "d"
// };
//
// Period.propTypes = {
//     initialDate: PropTypes.instanceOf(moment).isRequired,
//     title: PropTypes.string.isRequired,
//     handleChange: PropTypes.func.isRequired,
//     period: PropTypes.string,
//     max: PropTypes.instanceOf(moment),
//     min: PropTypes.instanceOf(moment)
// };

export default Period;
