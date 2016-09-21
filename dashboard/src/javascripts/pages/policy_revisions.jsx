import React from "react";

class PolicyRevisions extends React.Component {
  constructor() {
    super()

    this.state = {data: []};
  }

  render() {
    return (
      <div className="l-grid main">
        <div className="l-col-6">
          <div className="mod-policy-revisions">
            <h1>{I18n.t("revisions.title")}</h1>
            <form>
              {this.renderRevisions()}
            </form>
          </div>
        </div>
        <div className="l-col-6">
          {this.renderComparePanel()}
        </div>
      </div>
    );
  }

  renderRevisions() {
    this.props.revisions.sort(function (rev1, rev2) {
      return rev2.created - rev1.created;
    });
    return this.props.revisions.map(function (revision, index) {
      return this.renderRevision(revision, index);
    }.bind(this));
  }

  renderRevision(revision, index) {
    var classNameStatus = index === 0 ? "success" : "failure";
    var linkClassName = this.state.curr && this.state.curr.revisionNbr === revision.revisionNbr ? "selected" : "";
    return (
        <div className="form-element" key={index}>
          <fieldset className={classNameStatus}>
            <div className="l-grid">
              <div className="l-col-10">
                {this.renderRevisionMetadata(revision)}
              </div>
              <div className="l-col-2 no-gutter text-right">
                <a className={"c-button white compare "+linkClassName} href="#" onClick={this.handleCompare(revision)}>&lt; &gt;</a>
              </div>
            </div>
          </fieldset>
        </div>
    );
  }

  renderRevisionMetadata(revision) {
    return (
      <div>
        <p className="label before-em">{revision.name}</p>
        <p className="before-em">{I18n.t("revisions.revision") + " " + revision.revisionNbr}</p>
        <p className="before-em smaller">{I18n.t("policy_detail.sub_title", {displayName: revision.userDisplayName, created: this.createdDate(revision)})}</p>
      </div>
    );
  }

  createdDate(revision) {
    var created = moment(revision.created);
    created.locale(I18n.locale);
    return created.format('LLLL');
  }

  handleCompare(revision) {
    return function (e) {
      e.preventDefault();
      e.stopPropagation();
      var prev = this.props.revisions.filter(function (rev) {
        return rev.revisionNbr === (revision.revisionNbr - 1)
      });
      this.setState({curr: revision});
      this.setState({prev: prev[0]});
    }.bind(this);
  }

  renderComparePanel() {
    var prev = this.state.prev;
    var curr = this.state.curr;
    if (prev || curr) {
      return this.renderDiff(prev, curr);
    } else {
      return this.renderAboutPage();
    }
  }

  renderDiff(prev, curr) {
   var properties = [
      "name", "description", "denyRule", "serviceProviderName", "identityProviderNames",
      "allAttributesMustMatch", "attributes", "denyAdvice", "denyAdviceNl", "active"
    ];
    //means someone if looking at the first initial revision
    if (!prev) {
      prev = {attributes: []};
    }

    var renderPropertyDiff = function (prev, curr, name) {
      var diffElement = name === "attributes" ?
        this.renderAttributesDiff(prev, curr) :
        <div className={"diff-element " + this.classNamePropertyDiff(prev[name], curr[name])}>
          <p className="label">{I18n.t("revisions." + name)}</p>
          {this.renderPropertyDiff(prev[name], curr[name])}
        </div>;

      return (
        <div className="diff-container">
          { diffElement }
        </div>
      );
    }.bind(this);

    return (
      <div className="mod-policy-revisions-diff">
        {this.renderTopDiff(prev, curr)}
        <div className="diff-panel">
          {
            properties.map(function (prop) {
              return renderPropertyDiff(prev, curr, prop);
            })
          }
        </div>
      </div>
    );
  }

  renderPropertyDiff(prev, curr) {
    var previous = _.isArray(prev) ? prev.join(", ") : prev;
    var current = _.isArray(curr) ? curr.join(", ") : curr;
    if (previous === current) {
      return (<span className="diff no-change">{current.toString()}</span>)
    } else if (previous === undefined) {
      return <span className="diff curr">{current.toString()}</span>
    } else {
      return (<div>
        <span className="diff prev">{previous.toString()}</span>
        <span className="diff curr">{current.toString()}</span>
      </div>)
    }
  }

  classNamePropertyDiff(prev, curr) {
    var previous = _.isArray(prev) ? prev.join(", ") : prev;
    var current = _.isArray(curr) ? curr.join(", ") : curr;
    return previous !== current ? "changed" : "no-change";
  }

  renderTopDiff(prev, curr) {
    var translationKey = prev.revisionNbr !== undefined && prev.revisionNbr !== curr.revisionNbr ? "revisions.changes_info_html" : "revisions.changes_first_html";

    var topDiffHtml =
      I18n.t(translationKey, {
        userDisplayName: curr.userDisplayName,
        authenticatingAuthorityName: curr.authenticatingAuthorityName,
        createdDate: this.createdDate(curr),
        currRevisionNbr: curr.revisionNbr,
        prevRevisionNbr: prev.revisionNbr
      });

    return (
      <div className="top-diff" dangerouslySetInnerHTML={{__html:topDiffHtml}} />
    );
  }

  renderAttributesDiff(prev, curr) {
    var attrPrevGrouped = _.groupBy(prev.attributes, function (attr) {
      return attr.name;
    });

    var attrCurrGrouped = _.groupBy(curr.attributes, function (attr) {
      return attr.name;
    });

    var attrResult = _.reduce(attrCurrGrouped, function (result, attributes, attrName) {
      if (attrPrevGrouped.hasOwnProperty(attrName)) {
        //find out the diff in values
        var prevValues = _.pluck(attrPrevGrouped[attrName], 'value');
        var currValues = _.pluck(attributes, 'value');

        var deleted = _.difference(prevValues, currValues).map(function (deletedValue) {
          return {value: deletedValue, status: "prev"};
        });
        var added = _.difference(currValues, prevValues).map(function (addedValue) {
          return {value: addedValue, status: "curr"};
        });
        var unchanged = currValues.filter(function (value) {
          return prevValues.indexOf(value) !== -1;
        }).map(function (unchangedValue) {
          return {value: unchangedValue, status: "no-change"};
        });

        var newValues = deleted.concat(added).concat(unchanged);
        var anyValuesChanged = newValues.filter(function (val) {
          return val.status == "prev" || val.status === "curr";
        }).length > 0;

        result[attrName] = {values: newValues, status: "no-change", anyValuesChanged: anyValuesChanged};
        return result;
      } else {
        // these are the added attributes that are in curr and not in prev
        result[attrName] = {values: attributes.map(function (attribute) {
          return {value: attribute.value, status: "curr"};
        }), status: "curr"}

        return result;
      }
    }, {});

    var prevNames = Object.keys(attrPrevGrouped);

    // add the deleted attributes that are in prev and not in curr
    prevNames.forEach(function (name) {
      if (!attrResult.hasOwnProperty(name)) {
        attrResult[name] = {values: attrPrevGrouped[name].map(function (attribute) {
          return {value: attribute.value, status: "prev"};
        }), status: "prev"}
      }
    });

    var attributesUnchanged = _.values(attrResult).filter(function (attribuut) {
      return (attribuut.status === "prev" || attribuut.status === "curr") && attribuut.values.filter(function (value) {
        return value.value === "prev" || value.value === "curr";
      }).length === 0;
    }).length === 0 ;

    var attributeNames = Object.keys(attrResult);
    return (
        <div className={"diff-element " + (attributesUnchanged ? "no-change" : "changed")}>
          <p className="label">{I18n.t("revisions.attributes")}</p>
          {
            attributeNames.map(function (attributeName) {
              return (
                <div key={attributeName}>
                  <div className="attribute-container">
                    <span className={"diff " + attrResult[attributeName].status}>{attributeName}</span>
                  </div>
                  <div className={"attribute-values-container " + (attrResult[attributeName].status === "no-change"
                                      && attrResult[attributeName].anyValuesChanged ? "diff-element changed" : "")}>
                    <p className="label">{I18n.t("policy_attributes.values")}</p>
                    {
                      attrResult[attributeName].values.map(function (value) {
                        return (
                          <div className="value-container"
                               key={attributeName + "-" +
                                    attrResult[attributeName].status + "-" +
                                    value.value + "-" +
                                    value.status}>
                            <span className={"diff "+value.status}>{value.value}</span>
                          </div>
                        );
                      })
                    }
                  </div>
                </div>
              );
            })
          }
        </div>);
  }

  renderAboutPage() {
    return (
      <div className="mod-policy-revisions-about">
        {I18n.locale === "en" ? <App.Help.PolicyRevisionsHelpEn/> : <App.Help.PolicyRevisionsHelpNl/>}
      </div>
    );
  }
}

export default PolicyRevisions;
