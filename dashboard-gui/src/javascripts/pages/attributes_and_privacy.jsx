import React, {useContext} from 'react'
import I18n from 'i18n-js'
import {CurrentUserContext} from '../App'
import {privacyProperties} from '../utils/privacy'
import {marked} from 'marked'
import Tooltip from "../components/tooltip"
import 'github-markdown-css/github-markdown.css'

export default function AttributesAndPrivacy({app}) {
    const {currentUser} = useContext(CurrentUserContext)
    const hasPrivacyInfo = privacyProperties.some((prop) => app.privacyInfo[prop])
    const nameIdValue = app.nameIds.filter((val) => val.includes('unspecified') || val.includes('persistent')).length
        ? true
        : null
    return (
        <div className="app-detail-content attributes-privacy">
            <h2>{I18n.t('attributes_policy_panel.title')}</h2>
            <AttributeReleasePolicy app={app} nameIdValue={nameIdValue} currentUser={currentUser}/>
            {(app.manipulation && !currentUser.guest) && <ManipulationNotes app={app}/>}
            {hasPrivacyInfo && <PrivacyInfo app={app}/>}
        </div>
    )
}

function AttributeReleasePolicy({app, nameIdValue, currentUser}) {

    if (app.arp.noArp && !nameIdValue) {
        return <p>{I18n.t('attributes_policy_panel.arp.noarp', {name: app.name})}</p>
    } else if (app.arp.noAttrArp && !nameIdValue) {
        return <p>{I18n.t('attributes_policy_panel.arp.noattr', {name: app.name})}</p>
    }
    const nameId = app.nameIds[0];
    if (nameIdValue && !app.filteredUserAttributes.some(attr => attr.name === nameId)) {
        app.filteredUserAttributes.push({
            name: nameId,
            filters: ["*"],
            userValues: []
        })
        if (!app.motivations) {
            app.motivations = {}
        }
        if (!app.sources) {
            app.sources = {}
        }
        app.motivations[nameId] = I18n.t('attributes_policy_panel.persistentMotivation')
        app.sources[nameId] = 'surf';
    }
    return (
        <div>
            <p>{I18n.t('attributes_policy_panel.subtitle', {name: app.name})}</p>
            <table className="attributes">
                <thead>
                <tr>
                    <th scope="col" className="attribute">
                        {I18n.t('attributes_policy_panel.attribute')}
                    </th>
                    <th scope="col" className="source">
                        <span className={"th-container"}>
                            {I18n.t('attributes_policy_panel.source')}
                            <Tooltip id="source" text={I18n.t('attributes_policy_panel.sourceTooltip')}/>
                        </span>
                    </th>
                    <th scope="col" className="filter">
                        <span className={"th-container"}>
                            {I18n.t('attributes_policy_panel.filter')}
                            <Tooltip id="filter" text={I18n.t('attributes_policy_panel.filterTooltip')}/>
                        </span>
                    </th>
                    <th scope="col" className="motivation">
                        <span className={"th-container"}>
                            {I18n.t('attributes_policy_panel.motivation')}
                            <Tooltip id="motivation" text={I18n.t('attributes_policy_panel.motivationTooltip')}/>
                        </span>
                    </th>
                    <th className="value">
                        {!currentUser.guest && <span className={"th-container"}>
                        {I18n.t('attributes_policy_panel.your_value')}
                            <Tooltip id="values" text={I18n.t('attributes_policy_panel.your_values_tooltip')}/>
                            </span>}
                    </th>
                </tr>
                </thead>
                <tbody>
                {app.filteredUserAttributes
                    .map(attr => {
                        attr.description = I18n.t('attributes_policy_panel.attributes')[attr.name]
                        return attr
                    })
                    .sort((a1, a2) => (a1.description || "").localeCompare((a2.description || "")))
                    .map((attribute) => (
                        <Attribute app={app} attribute={attribute} currentUser={currentUser} key={attribute.name}/>
                    ))}
                </tbody>
            </table>
        </div>
    )
}

function Name({attribute}) {
    return <div className="attribute-name">
        <span>{attribute.description}</span>
        <span className="name-urn">{attribute.name}</span>
    </div>
}

function Attribute({attribute, app, currentUser}) {
    const renderFilters = attribute.filters.filter((flt) => flt !== '*')

    const name = attribute.name;
    const source = name === 'urn:mace:dir:attribute-def:eduPersonTargetedID' ? 'surf' : app.sources[name];

    return (
        <tr key={name}>
            <td>
                <Name attribute={attribute}/>
            </td>
            <td>{I18n.t(`attributes_policy_panel.sources.${source}`)}</td>
            <td>
                {renderFilters.length > 0 &&
                    <ul className="filters">
                        {renderFilters.map((filter, i) =>
                            <li key={i} className="filter">
                                {filter}
                            </li>
                        )}
                    </ul>
                }
            </td>
            <td>{app.motivations[name]}</td>

            <td>
                {(!currentUser.guest && source === 'idp') && <ul>
                    {attribute.userValues.map((val) => <li key={val}>{val}</li>)}
                </ul>}
            </td>

        </tr>
    )
}

function PrivacyInfo({app}) {
    return (
        <div className="privacy">
            <h2>{I18n.t('privacy_panel.title')}</h2>
            <p>{I18n.t('privacy_panel.subtitle', {name: app.name})}</p>
            <p>{I18n.t('privacy_panel.subtitle2', {name: app.name})}</p>
            <PrivacyTable app={app}/>
        </div>
    )
}

function PrivacyTable({app}) {
    return (
        <table>
            <thead>
            <tr>
                <th className="question">{I18n.t('privacy_panel.question')}</th>
                <th className="answer">{I18n.t('privacy_panel.answer')}</th>
            </tr>
            </thead>
            <tbody>
            {privacyProperties
                .filter(prop => !prop.startsWith('privacyStatementURL') || prop === `privacyStatementURL${I18n.locale}`)
                    .map((prop) => (
                        <PrivacyProp name={prop} prop={app.privacyInfo[prop]} key={prop}/>
                    ))}
            </tbody>
        </table>
    )
}

function PrivacyProp({name, prop}) {
    const noValue = prop === undefined || prop === null
    let value
    if (name === 'dpaType') {
        value = I18n.t(`privacy_panel.dpaTypeEnum.${prop}`)
    } else {
        value = prop
    }
    return (
        <tr key={name} className="privacy-row">
            <td>{I18n.t(`privacy_panel.${name}`)}</td>
            <td className="value">{noValue ? <em>{I18n.t('privacy_panel.noInformation')}</em> : value}</td>
        </tr>
    )
}

function ManipulationNotes({app}) {
    const notes = app.manipulationNotes && marked(app.manipulationNotes).replace(/<a href/g, '<a target="_blank" href')
    let title = I18n.t('attributes_policy_panel.arp.manipulation')
    if (app.manipulationNotes) {
        title += I18n.t('attributes_policy_panel.arp.manipulationNotes')
    }
    return (
        <div className="manipulation-notes">
            <p className="title">
                <i className="fa fa-warning"/>{title}
            </p>
            {app.manipulationNotes &&
                <section className="notes markdown-body" dangerouslySetInnerHTML={{__html: notes}}/>}

        </div>
    )
}
