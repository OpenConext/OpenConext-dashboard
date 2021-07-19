import React from 'react'
import I18n from 'i18n-js'
import { ReactComponent as LicenseIcon } from '../../images/common-file-text.svg'
import { ReactComponent as QuestionIcon } from '../../images/question-circle.svg'
import ReactTooltip from 'react-tooltip'

export default function LicenseInfoText({ app }) {
  function determineLicenseText(app) {
    switch (app.licenseStatus) {
      case 'NO_LICENSE':
        return I18n.t('license_info.no_license')
      case 'NOT_NEEDED':
        return I18n.t('license_info.no_license_needed')
      case 'UNKNOWN':
        return I18n.t('license_info.unknown_license')
      case 'HAS_LICENSE_SURFMARKET':
        return I18n.t('license_info.has_license_surfmarket')
      case 'HAS_LICENSE_SP':
        return I18n.t('license_info.has_license_sp')
    }
  }

  function determineExplanation(app) {
    switch (app.licenseStatus) {
      case 'NO_LICENSE':
        return I18n.t('license_info_panel.no_license_description_html')
      case 'HAS_LICENSE_SURFMARKET':
        return I18n.t('license_info_panel.has_license_surfmarket_html')
      case 'HAS_LICENSE_SP':
        return app.serviceUrl
          ? I18n.t('license_info_panel.has_license_sp_html', {
              serviceName: app.name,
              serviceUrl: app.serviceUrl,
              organisation: app.organisation,
            })
          : I18n.t('license_info_panel.has_license_sp_html_no_service_url', {
              serviceName: app.name,
              organisation: app.organisation,
            })
    }
  }

  const explanation = determineExplanation(app)

  return (
    <div className="license">
      <LicenseIcon />
      <span data-for={app.id.toString()} data-tip className={`tooltip-trigger ${explanation && 'underline'}`}>
        {determineLicenseText(app)}
        {explanation && <QuestionIcon />}
      </span>
      <ReactTooltip
        id={app.id.toString()}
        type="info"
        class="tool-tip"
        effect="solid"
        multiline={true}
        delayHide={250}
        clickable
        disable={!explanation}
        place="bottom"
      >
        <span dangerouslySetInnerHTML={{ __html: explanation }} />
      </ReactTooltip>
    </div>
  )
}
