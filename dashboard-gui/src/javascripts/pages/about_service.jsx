import React, { useState, useEffect } from 'react'
import I18n from 'i18n-js'
import { useParams } from 'react-router-dom'
import { getApp, getIdps } from '../api'
import Breadcrumbs from '../components/breadcrumbs'
import ServiceHeader from '../components/service_header'
import Tab from '../components/tab'
import TabBar from '../components/tab_bar'

export default function AboutService() {
  const { id, type } = useParams()
  const [app, setApp] = useState(null)
  const [institutions, setInstitutions] = useState(null)

  async function fetchApp() {
    const data = await getApp(id, type)
    setApp(data.payload)
    const idpData = await getIdps(data.payload.spEntityId, type)
    setInstitutions(idpData.payload)
  }

  useEffect(() => {
    fetchApp()
  }, [])

  if (!app) {
    return null
  }

  const breadcrumbs = [
    { link: '/apps/connected', text: 'Home' },
    { link: `/apps/${id}/${type}/about`, text: app.name },
  ]

  return (
    <div className="app-detail">
      <Breadcrumbs items={breadcrumbs} />
      <ServiceHeader app={app} />
      <TabBar>
        <Tab active to={`/apps/${id}/${type}/about`}>
          {I18n.t('apps.tabs.about')}
        </Tab>
        <Tab to="/apps/all">{I18n.t('apps.tabs.attributes')}</Tab>
        <Tab to="/apps/all">{I18n.t('apps.tabs.resource_servers')}</Tab>
        <Tab to="/apps/all">{I18n.t('apps.tabs.usage')}</Tab>
        <Tab to="/apps/all">{I18n.t('apps.tabs.settings')}</Tab>
      </TabBar>
      <div className="container">
        <div className="app-detail-content">
          <div className="top-content">
            <div className="left-side">
              <h2>{I18n.t('apps.detail.about')}</h2>
              <p className="description">{app.description}</p>
              <div className="contractual-base">
                <div className="contractual-base-header">
                  <strong>{I18n.t('apps.overview.contractualBase')}</strong>
                </div>
                <div className="contractual-base-content">
                  <p>
                    <span
                      dangerouslySetInnerHTML={{
                        __html: I18n.t(`overview_panel.contractualBase.${app.contractualBase.toLowerCase()}`, {
                          organisation: app.organisation,
                        }),
                      }}
                    />
                    &nbsp;
                    <span dangerouslySetInnerHTML={{ __html: I18n.t('overview_panel.contractualBaseWiki') }} />
                  </p>
                  <p>({I18n.t('overview_panel.vendorInfo', { organisation: app.organisation })})</p>
                </div>
              </div>
              {(app.entityCategories1 || app.entityCategories2 || app.entityCategories3) && (
                <div className="contractual-base">
                  <div className="contractual-base-header">
                    <strong>{I18n.t('overview_panel.entity_categories')}</strong>
                  </div>
                  <div className="contractual-base-content">
                    <EntityCategoryRow category={app.entityCategories1} />
                    <EntityCategoryRow category={app.entityCategories2} />
                    <EntityCategoryRow category={app.entityCategories3} />
                  </div>
                </div>
              )}
            </div>
            <div className="right-side">
              <div className="links">
                <h2>{I18n.t('apps.detail.links')}</h2>
                <ExternalURL name="website" link={app.websiteUrl} />
                <ExternalURL name="support" link={app.supportUrl} />
                <ExternalURL name="login" link={app.appUrl} />
                <ExternalURL name="eula" link={app.eulaUrl} />
                <ExternalURL name="registration_policy" link={app.registrationPolicyUrl} />
                <ExternalURL name="privacy_statement" link={app.privacyStatementUrl} />
              </div>
              {app.registrationInfoUrl && (
                <div className="federation-source">
                  <h2>{I18n.t('overview_panel.interfed_source')}</h2>
                  <span
                    dangerouslySetInnerHTML={{
                      __html: I18n.t('app_meta.registration_info_html', { url: app.registrationInfoUrl }),
                    }}
                  />
                </div>
              )}
            </div>
          </div>
          <div className="institutions">
            <InstitutionTable institutions={institutions} app={app} />
          </div>
        </div>
      </div>
    </div>
  )
}

function InstitutionTable({ institutions, app }) {
  if (!institutions) {
    return null
  }

  return (
    <div className="institutions-overview">
      <h2>{I18n.t('apps.detail.institutions_header.other', { count: institutions.length })}</h2>
      {institutions.length > 0 && (
        <>
          <p>{I18n.t('apps.detail.provided_information', { organisation: app.organisation })}</p>
          <div className="institutions-container">
            <div className="institutions-header">{I18n.t('apps.detail.institutions')}</div>
            <div className="institution-details">
              {institutions
                .sort((a, b) => a.name.localeCompare(b.name))
                .map((institution) => (
                  <div key={institution.id} className="institution">
                    <img src={institution.logoUrl} alt={institution.name} /> {institution.name}
                  </div>
                ))}
            </div>
          </div>
        </>
      )}
    </div>
  )
}

function EntityCategoryRow({ category }) {
  if (!category) {
    return null
  }

  return (
    <div>
      <a href={category} target="_blank" rel="noopener noreferrer">
        {I18n.t(`overview_panel.entity_category.${category.replace(/\./g, '')}`)}
      </a>
    </div>
  )
}

function ExternalURL({ name, link }) {
  if (!link) {
    return null
  }

  return (
    <div className="external-url">
      <a href={link} target="_blank" rel="noreferrer noopener">
        {I18n.t('app_meta.' + name)}
      </a>
    </div>
  )
}
