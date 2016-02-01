/** @jsx React.DOM */

App.Pages.PolicyRevisions = React.createClass({
  getInitialState: function () {
    return {data: []};
  },

  render: function () {
    return (
      <div className="l-grid main">
        <div className="l-col-6">
          <div className="mod-policy-revisions">
            <h1>{I18n.t("revisions.title")}</h1>
            {this.renderRevisions()}
          </div>
        </div>
        <div className="l-col-6">
          <div className="mod-policy-revisions-help">
            {this.renderComparePanel()}
          </div>
        </div>
      </div>
    );
  },

  renderRevisions: function () {
    this.props.revisions.sort(function (rev1, rev2) {
      return rev2.created - rev1.created;
    });
    return this.props.revisions.map(function (revision, index) {
      return this.renderRevision(revision, index);
    }.bind(this));
  },

  renderRevision: function (revision, index) {
    var classNameStatus = index === 0 ? "success" : "failure";
    var linkClassName = this.state.curr && this.state.curr.revisionNbr === revision.revisionNbr ? "selected" : "";
    return (
        <div className="form-element" key={index}>
          <fieldset className={classNameStatus}>
            {this.renderRevisionMetadata(revision)}
            <a className={"c-button white compare "+linkClassName} href="#" onClick={this.handleCompare(revision)}>&lt; &gt;</a>
          </fieldset>
        </div>
    );
  },

  renderRevisionMetadata: function (revision) {
    return (
      <div>
        <p className="label before-em">{revision.name}</p>
        <p className="before-em">{I18n.t("revisions.revision") + " " + revision.revisionNbr}</p>
        <p className="before-em smaller">{I18n.t("policy_detail.sub_title", {displayName: revision.userDisplayName, created: this.createdDate(revision)})}</p>
      </div>
    );
  },

  createdDate: function (revision) {
    var created = moment(revision.created);
    created.locale(I18n.locale);
    return created.format('LLLL');
  },

  handleCompare: function (revision) {
  },

  renderComparePanel: function () {
    var prev = this.state.prev;
    var curr = this.state.curr;
    if (prev || curr) {
      return this.renderDiff(prev, curr);
    } else {
      return this.renderAboutPage();
    }
  },

  renderDiff: function (prev, curr) {
  },

  renderAboutPage: function () {
    return I18n.locale === "en" ? <App.Help.PolicyRevisionsHelpEn/> : <App.Help.PolicyRevisionsHelpNl/>;
  },

})
