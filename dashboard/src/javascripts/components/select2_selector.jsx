/** @jsx React.DOM */

App.Components.Select2Selector = React.createClass({

  getInitialState: function () {
    return {
      value: this.props.value
    }
  },

  componentDidMount: function () {
    var rootNode = $('[data-select2selector-id="' + this.props.select2selectorId + '"]');
    var minimumResultsForSearch = this.props.minimumResultsForSearch || 7
    rootNode.select2({
      width: '100%',
      forceBelow: true,
      minimumResultsForSearch: minimumResultsForSearch
    });
    rootNode.val(this.props.value).trigger("change");
    // This is not the react way, but this react version does not support native Select2 ports
    rootNode.on("change", this.handleChange);
  },

  shouldComponentUpdate: function (nextProps, nextState) {
    return nextProps.value != this.state.value;
  },

  componentDidUpdate: function (prevProps, prevState) {
    if (this.state.value === this.props.value) {
      return;
    }
    var rootNode = $('[data-select2selector-id="' + this.props.select2selectorId + '"]');
    rootNode.val(this.props.value).trigger("change");
  },

  componentWillUnmount: function () {
    var rootNode = $('[data-select2selector-id="' + this.props.select2selectorId + '"]');
    rootNode.select2("destroy");
  },

  handleChange: function (e) {
    var newValue = e.target.value;
    this.setState({value: newValue});
    this.props.handleChange(newValue);
  },

  render: function () {
    var renderOption = this.props.options.map(function (option, index) {
      return (<option key={option.value} value={option.value}>{option.display}</option>);
    });
    return (
        <div>
          <select id="lang" data-select2selector-id={this.props.select2selectorId}>
            {renderOption}
          </select>
        </div>
    );
  }
});
