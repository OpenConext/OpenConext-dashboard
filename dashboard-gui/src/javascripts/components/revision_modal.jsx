import React from 'react'
import I18n from 'i18n-js'
import moment from 'moment'
import ConnectModalContainer from './connect_modal_container'
import values from 'lodash.values'
import groupBy from 'lodash.groupby'
import reduce from 'lodash.reduce'
import map from 'lodash.map'
import difference from 'lodash.difference'

const properties = [
  'name',
  'description',
  'denyRule',
  'serviceProviderNames',
  'identityProviderNames',
  'allAttributesMustMatch',
  'attributes',
  'denyAdvice',
  'denyAdvicePt',
  'denyAdviceNl',
  'active',
]

export default function RevisionModal({ isOpen, onClose, current, previous }) {
  if (!current) {
    return null
  }
  function createdDate(revision) {
    if (revision.created) {
      const created = moment(revision.created)
      created.locale(I18n.locale)
      return created.format('LLLL')
    }
    return ''
  }
  if (!previous) {
    previous = { attributes: [] }
  }
  const translationKey =
    previous.revisionNbr !== undefined && previous.revisionNbr !== current.revisionNbr
      ? 'revisions.changes_info_html'
      : 'revisions.changes_first_html'

  const topDiffHtml = I18n.t(translationKey, {
    userDisplayName: current.userDisplayName,
    authenticatingAuthorityName: current.authenticatingAuthorityName,
    createdDate: createdDate(current),
    currRevisionNbr: current.revisionNbr,
    prevRevisionNbr: previous.revisionNbr,
  })

  return (
    <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
      <div className="revision-modal">
        <div className="connect-modal-header">
          <div className="top-diff" dangerouslySetInnerHTML={{ __html: topDiffHtml }} />
        </div>
        <div className="connect-modal-body">
          {properties.map((prop) => {
            return <PropertyDiff previous={previous} current={current} property={prop} key={prop} />
          })}
        </div>
        <div className="buttons">
          <button className="c-button white" onClick={onClose}>
            {I18n.t('how_to_connect_panel.close')}
          </button>
        </div>
      </div>
    </ConnectModalContainer>
  )
}

function PropertyDiff({ previous, current, property }) {
  if (property === 'attributes') {
    return <AttributesDiff previous={previous} current={current} />
  }
  const propertyKey = property === 'serviceProviderNames' && (!previous[property] || previous[property].length === 0) ? 'serviceProviderName' : property;
  return (
    <div className="diff-container">
      <div className="diff-element">
        <label>{I18n.t('revisions.' + propertyKey)}</label>
        <SinglePropertyDiff prev={previous[propertyKey]} curr={current[propertyKey]} />
      </div>
    </div>
  )
}

function SinglePropertyDiff({ prev, curr }) {
  const previous = Array.isArray(prev) ? prev.join(', ') : prev
  const current = Array.isArray(curr) ? curr.join(', ') : curr
  if (previous === current) {
    return <span className="diff no-change">{current !== undefined && current.toString()}</span>
  } else if (previous === undefined) {
    return <span className="diff curr">{current !== undefined && current.toString()}</span>
  }

  return (
    <div className="diff-changed">
      <span className="diff prev">{previous !== undefined && previous.toString()}</span>

      <span className="diff curr">{current !== undefined && current.toString()}</span>
    </div>
  )
}

function AttributesDiff({ previous, current }) {
  const attrPrevGrouped = groupBy(previous.attributes, (attr) => {
    return attr.name
  })

  const attrCurrGrouped = groupBy(current.attributes, (attr) => {
    return attr.name
  })

  const attrResult = reduce(
    attrCurrGrouped,
    (result, attributes, attrName) => {
      if (Object.prototype.hasOwnProperty.call(attrPrevGrouped, attrName)) {
        //find out the diff in values
        const prevValues = map(attrPrevGrouped[attrName], 'value')
        const currValues = map(attributes, 'value')

        const deleted = difference(prevValues, currValues).map((deletedValue) => {
          return { value: deletedValue, status: 'prev' }
        })
        const added = difference(currValues, prevValues).map((addedValue) => {
          return { value: addedValue, status: 'curr' }
        })
        const unchanged = currValues
          .filter((value) => {
            return prevValues.indexOf(value) !== -1
          })
          .map((unchangedValue) => {
            return { value: unchangedValue, status: 'no-change' }
          })

        const newValues = deleted.concat(added).concat(unchanged)
        const anyValuesChanged =
          newValues.filter((val) => {
            return val.status === 'prev' || val.status === 'curr'
          }).length > 0

        result[attrName] = { values: newValues, status: 'no-change', anyValuesChanged: anyValuesChanged }

        return result
      }

      // these are the added attributes that are in curr and not in prev
      result[attrName] = {
        values: attributes.map((attribute) => {
          return { value: attribute.value, status: 'curr' }
        }),
        status: 'curr',
      }

      return result
    },
    {}
  )

  const prevNames = Object.keys(attrPrevGrouped)

  // add the deleted attributes that are in prev and not in curr
  prevNames.forEach((name) => {
    if (!Object.prototype.hasOwnProperty.call(attrResult, name)) {
      attrResult[name] = {
        values: attrPrevGrouped[name].map((attribute) => {
          return { value: attribute.value, status: 'prev' }
        }),
        status: 'prev',
      }
    }
  })

  const attributesUnchanged =
    values(attrResult).filter((attribuut) => {
      return (
        (attribuut.status === 'prev' || attribuut.status === 'curr') &&
        attribuut.values.filter((value) => {
          return value.value === 'prev' || value.value === 'curr'
        }).length === 0
      )
    }).length === 0

  const attributeNames = Object.keys(attrResult)
  return (
    <div className="diff-container">
      <div className={'diff-element ' + (attributesUnchanged ? 'no-change' : 'changed')}>
        <label>{I18n.t('revisions.attributes')}</label>
        {attributeNames.map((attributeName) => {
          return (
            <div key={attributeName}>
              <div className="attribute-container">
                <span className={'diff ' + attrResult[attributeName].status}>{attributeName}</span>
              </div>
              <div
                className={
                  'attribute-values-container ' +
                  (attrResult[attributeName].status === 'no-change' && attrResult[attributeName].anyValuesChanged
                    ? 'diff-element changed'
                    : '')
                }
              >
                <label>{I18n.t('policy_attributes.values')}</label>
                {attrResult[attributeName].values.map((value) => {
                  return (
                    <div
                      className="value-container"
                      key={
                        attributeName + '-' + attrResult[attributeName].status + '-' + value.value + '-' + value.status
                      }
                    >
                      <span className={'diff ' + value.status}>{value.value}</span>
                    </div>
                  )
                })}
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}
