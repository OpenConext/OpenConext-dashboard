import React, { useContext, useEffect, useState } from 'react'
import I18n from 'i18n-js'
import { Link } from 'react-router-dom'
import { getInstitutionServiceProviders } from '../api'
import moment from 'moment'
import ReactTooltip from 'react-tooltip'
import groupBy from 'lodash.groupby'
import ContactPerson from '../components/contact_person'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faQuestionCircle } from '@fortawesome/free-regular-svg-icons'
import { faSearch } from '@fortawesome/free-solid-svg-icons'
import { CurrentUserContext } from '../App'

export default function SerivcesForIdp() {
  const [services, setServices] = useState([])
  const [searchQuery, setSearchQuery] = useState('')
  const [loading, setLoading] = useState(false)

  async function fetchServiceProviders() {
    const data = await getInstitutionServiceProviders()
    setServices(data.payload)
    setLoading(false)
  }

  useEffect(() => {
    fetchServiceProviders()
  }, [])

  const filteredServices =
    searchQuery.length > 0
      ? services.filter(
          (service) =>
            Object.values(service.names).some((name) => name.toLowerCase().indexOf(searchQuery) > -1) ||
            service.spEntityId.toLowerCase().indexOf(searchQuery) > -1
        )
      : services

  return (
    <div className="services-overview">
      <div className="title-container">
        <h2>{I18n.t('my_idp.services_title')}</h2>
        <div className="search-container">
          <input
            type="search"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value.trim().toLowerCase())}
            placeholder={I18n.t('apps.overview.search_hint')}
          />
          <FontAwesomeIcon icon={faSearch} />
        </div>
      </div>
      {!loading && filteredServices.length === 0 && <span>{I18n.t('my_idp.services_title_none')}</span>}
      {filteredServices
        .sort((a, b) => a.name.localeCompare(b.name))
        .map((service) => (
          <Service key={service.id} service={service} />
        ))}
    </div>
  )
}

function EditIdpButton() {
  return (
    <Link className="c-button" to={'/my-idp/edit'}>
      {I18n.t('my_idp.edit')}
    </Link>
  )
}

function Service({ service }) {
  const { currentUser } = useContext(CurrentUserContext)
  const isDashboardAdmin = currentUser.dashboardAdmin

  const groupedContactPersons = groupBy(service.contactPersons, (contactPerson) => {
    return `${contactPerson.name}-${contactPerson.emailAddress}-${contactPerson.telephoneNumber}`
  })

  const contactPersons = Object.values(groupedContactPersons).map((roleObjects) => {
    return {
      name: roleObjects[0].name,
      emailAddress: roleObjects[0].emailAddress,
      telephoneNumber: roleObjects[0].telephoneNumber,
      types: [...new Set(roleObjects.map((x) => x.contactPersonType))],
    }
  })

  return (
    <div className="service">
      <div className="header-with-button">
        <h3>
          <Link to={`/apps/${service.id}/${service.entityType}/about`}>{service.name}</Link>
        </h3>
        {isDashboardAdmin && <EditIdpButton />}
      </div>
      <div className="general-information">
        <div className="header-with-button">
          <h2>{I18n.t('my_idp.general_information')}</h2>
        </div>
        <table>
          <thead>
            <tr>
              <th scope="col"></th>
              <th scope="col">{I18n.t('my_idp.english')}</th>
              <th scope="col">{I18n.t('my_idp.dutch')}</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{I18n.t('my_idp.entity_id')}</td>
              <td className="bold">{service.spEntityId}</td>
              <td className="bold">{service.spEntityId}</td>
            </tr>
            <tr>
              <td>{I18n.t('my_idp.state')}</td>
              <td className="bold">{I18n.t('my_idp.' + service.state)}</td>
              <td className="bold">{I18n.t('my_idp.' + service.state)}</td>
            </tr>
            <tr>
              <td>{I18n.t('my_idp.name.general')}</td>
              <td className="bold">{service.names.en}</td>
              <td className="bold">{service.names.nl}</td>
            </tr>
            <tr>
              <td>{I18n.t('my_idp.description.general')}</td>
              <td className="bold">{service.descriptions.en}</td>
              <td className="bold">{service.descriptions.nl}</td>
            </tr>
            <tr>
              <td>{I18n.t('my_idp.displayName.general')}</td>
              <td className="bold">{service.displayNames.en}</td>
              <td className="bold">{service.displayNames.nl}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div className="settings">
        <div className="header-with-button">
          <h2>{I18n.t('my_idp.settings')}</h2>
        </div>
        <table>
          <tbody>
            <tr>
              <td>{I18n.t('my_idp.published_in_edugain')}</td>
              <td>
                <Badge enabled={service.publishedInEdugain} />
              </td>
            </tr>
            {service.publishedInEdugain && (
              <tr>
                <td>{I18n.t('my_idp.date_published_in_edugain')}</td>
                <td>{moment(service.publishInEdugainDate).locale(I18n.locale).format('LLLL')}</td>
              </tr>
            )}
            <tr>
              <td>{I18n.t('my_idp.guest_enabled')}</td>
              <td>
                <div className="tooltip-container">
                  <Badge enabled={service.guestEnabled} />
                  <div>
                    <FontAwesomeIcon icon={faQuestionCircle} data-tip data-for="displayGuestEnabled" />
                    <ReactTooltip
                      id="displayGuestEnabled"
                      type="info"
                      class="tool-tip"
                      effect="solid"
                      multiline
                      delayHide={250}
                      clickable
                    >
                      <span dangerouslySetInnerHTML={{ __html: I18n.t('my_idp.guest_enabled_tooltip') }} />
                    </ReactTooltip>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      {contactPersons && contactPersons.length > 0 && (
        <div className="contact-persons">
          <div className="header-with-button">
            <h2>{I18n.t('my_idp.contact')}</h2>
          </div>
          <div className="contact-persons-grid">
            {contactPersons.map((contactPerson, i) => (
              <ContactPerson contactPerson={contactPerson} key={i} />
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

function Badge({ enabled }) {
  return (
    <div className={`badge ${enabled ? 'enabled' : 'disabled'}`}>
      {enabled ? I18n.t('boolean.yes') : I18n.t('boolean.no')}
    </div>
  )
}
