import React, { useState, useEffect } from 'react'
import I18n from 'i18n-js'
import { getIdps } from '../api'
import { ReactComponent as WebsiteIcon } from '../../images/network-search.svg'
import { ReactComponent as SupportIcon } from '../../images/network-information.svg'
import { ReactComponent as LoginIcon } from '../../images/login-1.svg'
import { ReactComponent as EulaIcon } from '../../images/common-file-text-check.svg'
import { ReactComponent as RegistrationPolicyIcon } from '../../images/common-file-text-edit.svg'
import { ReactComponent as PrivacyStatementIcon } from '../../images/single-neutral-actions-text.svg'

export default function AboutService({ app, type, currentUser }) {
  const [institutions, setInstitutions] = useState(null)

  if (!app) {
    return null
  }

  async function fetchInstitutions() {
    const idpData = await getIdps(app.spEntityId, type)
    const filtered = idpData.payload.filter((idp) => idp.state === app.state)
    setInstitutions(filtered)
  }

  useEffect(() => {
    if (!currentUser.guest) {
      fetchInstitutions()
    }

  }, [])

  return (
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
          {app.entityType === 'single_tenant_template' && (
              <div className="single-tenant-info">
                <h3>{I18n.t('overview_panel.single_tenant_service')}</h3>
                <p className="explanation" dangerouslySetInnerHTML={{
                      __html: I18n.t('overview_panel.single_tenant_service_html', { name: app.name }),
                    }}
                />
              </div>
          )}
        </div>
        <div className="right-side">
          <div className="links">
            <h2>{I18n.t('apps.detail.links')}</h2>
            <ExternalURL name="website" link={app.websiteUrl}>
              <WebsiteIcon />
            </ExternalURL>
            <ExternalURL name="support" link={app.supportUrl}>
              <SupportIcon />
            </ExternalURL>
            <ExternalURL name="login" link={app.appUrl}>
              <LoginIcon />
            </ExternalURL>
            <ExternalURL name="eula" link={app.eulaUrl}>
              <EulaIcon />
            </ExternalURL>
            <ExternalURL name="registration_policy" link={app.registrationPolicyUrl}>
              <RegistrationPolicyIcon />
            </ExternalURL>
            <ExternalURL name="privacy_statement" link={app.privacyStatementUrl}>
              <PrivacyStatementIcon />
            </ExternalURL>
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
      {(app.entityType !== 'single_tenant_template' && institutions) &&
          <div className="institutions">
            <InstitutionTable institutions={institutions} />
          </div>}
    </div>
  )
}

function InstitutionTable({ institutions }) {
  if (!institutions) {
    return null
  }

  return (
    <div className="institutions-overview">
      <h2>{I18n.t('apps.detail.institutions_header.other', { count: institutions.length })}</h2>
      {institutions.length > 0 && (
        <>
          <div className="institutions-container">
            <div className="institutions-header">{I18n.t('apps.detail.institutions')}</div>
            <div className="institution-details">
              {institutions
                .sort((a, b) => a.name.localeCompare(b.name))
                .map((institution) => (
                  <div key={institution.id} className="institution">
                    {institution.logoUrl ? (
                      <img src={institution.logoUrl} alt="" />
                    ) : (
                      <div className="logo-placeholder" />
                    )}
                    {institution.name}
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

function ExternalURL({ name, link, children }) {
  if (!link) {
    return null
  }

  return (
    <div className="external-url">
      <a href={link} target="_blank" rel="noreferrer noopener">
        {children}
        {I18n.t('app_meta.' + name)}
      </a>
    </div>
  )
}
