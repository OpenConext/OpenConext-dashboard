import React from 'react'
import I18n from 'i18n-js'
import groupBy from 'lodash.groupby'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faTrashAlt } from '@fortawesome/free-solid-svg-icons'

import SelectWrapper from './select_wrapper'

export default function PolicyAttributeSelect({ policy, allowedAttributes, onChange }) {
  const newAttributeOptions = allowedAttributes.map((attribute) => ({
    value: attribute.name,
    display: attribute.name,
  }))

  function onNewAttribute(name) {
    onChange([...policy.attributes, { name, value: '' }])
  }

  function handleAttributeValueChanged(name, index, value) {
    let indexOfFound = 0

    const newAttributes = policy.attributes.map((attribute) => {
      if (attribute.name === name && indexOfFound === index) {
        attribute.value = value
      }

      if (attribute.name === name) {
        indexOfFound++
      }

      return attribute
    })

    onChange(newAttributes)
  }

  function removeAttribute(name) {
    const newAttributes = policy.attributes.filter((attribute) => attribute.name !== name)
    onChange(newAttributes)
  }

  function removeAttributeValue(name, index) {
    let indexOfFound = 0

    const newAttributes = policy.attributes.filter((attribute) => {
      if (attribute.name === name && indexOfFound === index) {
        indexOfFound++
        return false
      }
      if (attribute.name === name) {
        indexOfFound++
      }

      return true
    })

    onChange(newAttributes)
  }

  const grouped = groupBy(policy.attributes, (attr) => {
    return attr.name
  })
  const attrNames = Object.keys(grouped).sort()

  return (
    <div className="policy-attribute-select">
      {attrNames.map((attrName) => {
        return (
          <div key={attrName}>
            <div className="attribute-container">
              <input type="text" name="attribute" className="form-input disabled" value={attrName} disabled />
              <button onClick={() => removeAttribute(attrName)} className="c-button remove">
                <FontAwesomeIcon icon={faTrashAlt} />
              </button>
            </div>
            <div className="attribute-values">
              <label>{I18n.t('policy_attributes.values')}</label>
              <AttributeInfo name={attrName} />
              {grouped[attrName].map((attribute, index) => {
                return (
                  <div className="attribute-value-container" key={index}>
                    <input
                      key={index}
                      type="text"
                      className="form-input"
                      value={attribute.value}
                      placeholder={I18n.t('policy_attributes.attribute_value_placeholder')}
                      onChange={(e) => handleAttributeValueChanged(attrName, index, e.target.value)}
                    />
                    <button onClick={() => removeAttributeValue(attrName, index)} className="c-button remove">
                      <FontAwesomeIcon icon={faTrashAlt} />
                    </button>
                  </div>
                )
              })}
              <div onClick={() => onNewAttribute(attrName)} className="new-attribute-value" role="button" tabIndex={-1}>
                {I18n.t('policy_attributes.new_value')}
              </div>
            </div>
          </div>
        )
      })}
      <div className="new-attribute">
        <SelectWrapper
          value={null}
          placeholder={I18n.t('policy_attributes.new_attribute')}
          options={newAttributeOptions}
          handleChange={onNewAttribute}
          isClearable
        />
      </div>
    </div>
  )
}

function AttributeInfo({ name }) {
  if ('urn:collab:sab:surfnet.nl' === name) {
    return (
      <div className="attribute-value">
        <sup>*</sup>
        {I18n.t('policy_attributes.sab_info')}
      </div>
    )
  } else if ('urn:collab:group:surfteams.nl' === name) {
    return (
      <div className="attribute-value">
        <sup>*</sup>
        {I18n.t('policy_attributes.group_info')}
      </div>
    )
  }

  return null
}
