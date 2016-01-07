/** @jsx React.DOM */

App.Components.Period = React.createClass({
  getInitialState: function() {
    return {
      date: this.props.initialDate.format("YYYY-MM-DD"),
      error: false
    }
  },

  render: function () {
    return (
      <fieldset>
        <h2>{this.props.title}</h2>
        <input type="text" value={this.state.date} onChange={this.handlePeriodDateChanged} className={this.state.error ? 'error' : ''} />
      </fieldset>
    );
  },

  handlePeriodDateChanged: function(event) {
    event.preventDefault();
    event.stopPropagation();
    this.setState({date: event.target.value});
    var m = moment(event.target.value, "YYYY-MM-DD", true);
    if (m.isValid()) {
      this.setState({error: false});
      this.props.handleChange(m);
    } else {
      this.setState({error: true});
    }
  }
});
