import React from "react";

import moment from "moment";

class Period extends React.Component {
  constructor() {
    super();

    this.state = {
      date: moment(),
      error: false
    };
  }

  componentWillMount() {
    this.setState({ date: this.props.initialDate.format("YYYY-MM-DD")});
  }

  render() {
    return (
      <fieldset>
        <h2>{this.props.title}</h2>
        <input type="text" value={this.state.date} onChange={e => this.handlePeriodDateChanged(e)} className={this.state.error ? "error" : ""} />
      </fieldset>
    );
  }

  handlePeriodDateChanged(event) {
    event.preventDefault();
    event.stopPropagation();
    this.setState({date: event.target.value});
    const m = moment(event.target.value, "YYYY-MM-DD", true);
    if (m.isValid()) {
      this.setState({error: false});
      this.props.handleChange(m);
    } else {
      this.setState({error: true});
    }
  }
}

Period.propTypes = {
  initialDate: React.PropTypes.instanceOf(moment).isRequired,
  title: React.PropTypes.string.isRequired,
  handleChange: React.PropTypes.func.isRequired
};

export default Period;
