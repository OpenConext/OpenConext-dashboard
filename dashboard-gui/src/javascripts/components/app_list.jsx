import React, { useEffect, useState } from 'react'
import I18n from 'i18n-js'
import ReactTooltip from 'react-tooltip'
import includes from 'lodash.includes'
import isEmpty from 'lodash.isempty'
import { disableConsent } from '../api'
import { Link } from 'react-router-dom'
import { ReactComponent as ConnectedServiceIcon } from '../../images/tags-favorite-star.svg'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faSearch } from '@fortawesome/free-solid-svg-icons'

import Facets from '../components/facets'
import Pagination from '../components/pagination'
import { consentTypes } from '../utils/utils'

const PAGE_COUNT = 25

export default function AppList({ apps, currentUser, facets: remoteFacets }) {
  const [searchQuery, setSearchQuery] = useState('')
  const [activeFacets, setActiveFacets] = useState({})
  const [page, setPage] = useState(1)
  const [entityCategoriesFacetSelector, setEntityCategoriesFacetSelector] = useState(false)
  const [idpDisableConsent, setIdpDisableConsent] = useState([])

  async function fetchDisableConsent() {
    const value = await disableConsent()
    setIdpDisableConsent(value)
  }

  useEffect(() => {
    fetchDisableConsent()
  }, [])

  function handleFacetChange(facet, facetValue, checked) {
    const newFacets = { ...activeFacets }
    newFacets[facet] = newFacets[facet] || []

    if (checked) {
      newFacets[facet].push(facetValue)
    } else {
      newFacets[facet] = newFacets[facet].filter((value) => value !== facetValue)
    }

    setActiveFacets(newFacets)
    setPage(1)
  }

  function resetFilters() {
    setActiveFacets({})
    setPage(1)
  }

  function onSearch(e) {
    setSearchQuery(e.target.value.toLowerCase())
    setPage(1)
  }

  const stepupEntities = currentUser.getCurrentIdp().stepupEntities || []
  const arpAttributes = apps.reduce((acc, app) => {
    Object.keys(app.arp.attributes).forEach((attr) => {
      if (acc.indexOf(attr) < 0) {
        acc.push(attr)
      }
    })
    return acc
  }, [])

  function filterYesNoFacet(name, yes) {
    const values = activeFacets[name] || []
    return values.length === 0 || (yes && includes(values, 'yes')) || (!yes && includes(values, 'no'))
  }

  let facets = []
  facets.push({
    name: I18n.t('facets.static.connection.name'),
    searchValue: 'connection',
    values: [
      {
        value: I18n.t('facets.static.connection.has_connection'),
        searchValue: 'yes',
        count: (apps) => apps.filter((app) => app.connected).length,
      },
      {
        value: I18n.t('facets.static.connection.no_connection'),
        searchValue: 'no',
        count: (apps) => apps.filter((app) => !app.connected).length,
      },
    ],
    filterApp: function (app) {
      return filterYesNoFacet('connection', app.connected)
    },
  })
  facets.push({
    name: I18n.t('facets.static.used_by_idp.name'),
    searchValue: 'used_by_idp',
    values: [
      {
        value: I18n.t('facets.static.used_by_idp.yes'),
        searchValue: 'yes',
        count: (apps) => apps.filter((app) => currentUser.getCurrentIdp().institutionId === app.institutionId).length,
      },
      {
        value: I18n.t('facets.static.used_by_idp.no'),
        searchValue: 'no',
        count: (apps) => apps.filter((app) => currentUser.getCurrentIdp().institutionId !== app.institutionId).length,
      },
    ],
    filterApp: function (app) {
      return filterYesNoFacet('used_by_idp', currentUser.getCurrentIdp().institutionId === app.institutionId)
    },
  })
  facets.push({
    name: I18n.t('facets.static.interfed_source.name'),
    tooltip: I18n.t('facets.static.interfed_source.tooltip'),
    searchValue: 'interfed_source',
    values: [
      {
        value: I18n.t('facets.static.interfed_source.surfconext'),
        searchValue: 'SURFconext',
        count: (apps) => apps.filter((app) => app.interfedSource === 'SURFconext').length,
      },
      {
        value: I18n.t('facets.static.interfed_source.edugain'),
        searchValue: 'eduGAIN',
        count: (apps) => apps.filter((app) => app.interfedSource === 'eduGAIN').length,
      },
      {
        value: I18n.t('facets.static.interfed_source.entree'),
        searchValue: 'Entree',
        count: (apps) => apps.filter((app) => app.interfedSource === 'Entree').length,
      },
    ],
    filterApp: function (app) {
      const sourceFacetValues = activeFacets['interfed_source'] || []
      return sourceFacetValues.length === 0 || sourceFacetValues.indexOf(app.interfedSource) > -1
    }.bind(this),
  })
  const entityCategoryFacet = {
    name: I18n.t('facets.static.entity_category.name'),
    tooltip: I18n.t('facets.static.entity_category.tooltip'),
    searchValue: 'entity_category',
    values: [
      {
        value: I18n.t('facets.static.entity_category.code_of_conduct'),
        searchValue: 'http://www.geant.net/uri/dataprotection-code-of-conduct/v1',
      },
      {
        value: I18n.t('facets.static.entity_category.research_and_scholarship'),
        searchValue: 'http://refeds.org/category/research-and-scholarship',
      },
    ],
    filterApp: function (app) {
      const sourceFacetValues = activeFacets['entity_category'] || []
      if (sourceFacetValues.length === 0) {
        return true
      }
      if (entityCategoriesFacetSelector && sourceFacetValues.length === 2) {
        return sourceFacetValues.every(
          (sfc) => app.entityCategories1 === sfc || app.entityCategories2 === sfc || app.entityCategories3 === sfc
        )
      }
      return (
        sourceFacetValues.indexOf(app.entityCategories1) > -1 ||
        sourceFacetValues.indexOf(app.entityCategories2) > -1 ||
        sourceFacetValues.indexOf(app.entityCategories3) > -1
      )
    }.bind(this),
    extraContentRenderer: function extraContentRenderer() {
      return (
        <div className="entity-categories-facet-selector">
          <label>{I18n.t('facets.static.entity_category.selectAll')}</label>
          <input
            className={entityCategoriesFacetSelector ? 'checked' : 'unchecked'}
            checked={entityCategoriesFacetSelector}
            type="checkbox"
            onChange={() => {
              setEntityCategoriesFacetSelector(!entityCategoriesFacetSelector)
            }}
          />
          <i className="fa fa-info-circle absolute" data-for="entity-categories-facet-selector-tooltip" data-tip></i>
          <ReactTooltip
            id="entity-categories-facet-selector-tooltip"
            type="info"
            class="tool-tip"
            effect="solid"
            multiline={true}
          >
            <span dangerouslySetInnerHTML={{ __html: I18n.t('facets.static.entity_category.tooltipAll') }} />
          </ReactTooltip>
        </div>
      )
    },
  }

  entityCategoryFacet.values.forEach((value) => {
    value.count = (apps) =>
      apps.filter(
        (app) =>
          app.entityCategories1 === value.searchValue ||
          app.entityCategories2 === value.searchValue ||
          app.entityCategories3 === value.searchValue
      ).length
  })

  facets.push(entityCategoryFacet)
  facets.push({
    name: I18n.t('facets.static.license.name'),
    searchValue: 'license',
    values: [
      {
        value: I18n.t('facets.static.license.has_license_surfmarket'),
        searchValue: 'HAS_LICENSE_SURFMARKET',
        count: (apps) => apps.filter((app) => app.licenseStatus === 'HAS_LICENSE_SURFMARKET').length,
      },
      {
        value: I18n.t('facets.static.license.has_license_sp'),
        searchValue: 'HAS_LICENSE_SP',
        count: (apps) => apps.filter((app) => app.licenseStatus === 'HAS_LICENSE_SP').length,
      },
      {
        value: I18n.t('facets.static.license.not_needed'),
        searchValue: 'NOT_NEEDED',
        count: (apps) => apps.filter((app) => app.licenseStatus === 'NOT_NEEDED').length,
      },
      {
        value: I18n.t('facets.static.license.unknown'),
        searchValue: 'UNKNOWN',
        count: (apps) => apps.filter((app) => app.licenseStatus === 'UNKNOWN').length,
      },
    ],
    filterApp: function (app) {
      const licenseFacetValues = activeFacets['license'] || []
      return licenseFacetValues.length === 0 || licenseFacetValues.indexOf(app.licenseStatus) > -1
    },
  })
  const strongAuthenticationFacet = {
    name: I18n.t('facets.static.strong_authentication.name'),
    tooltip: I18n.t('facets.static.strong_authentication.tooltip'),
    searchValue: 'strong_authentication',
    values: currentUser.loaLevels
      .reduce((acc, loa) => {
        acc.push({ value: 'SP - ' + loa.substring(loa.lastIndexOf('/') + 1), searchValue: 'SP_' + loa })
        acc.push({ value: 'IDP - ' + loa.substring(loa.lastIndexOf('/') + 1), searchValue: 'IDP_' + loa })
        return acc
      }, [])
      .sort((a, b) => a.value.localeCompare(b.value))
      .concat([{ value: I18n.t('facets.static.strong_authentication.none'), searchValue: 'NONE' }]),
    filterApp: function (app) {
      const strongAuthenticationFacetValues = activeFacets['strong_authentication'] || []
      const minimalLoaLevel = 'SP_' + app.minimalLoaLevel
      const stepUpEntity = stepupEntities.find((e) => e.name === app.spEntityId)
      const none = strongAuthenticationFacetValues.indexOf('NONE') > -1
      return (
        strongAuthenticationFacetValues.length === 0 ||
        (none && isEmpty(app.minimalLoaLevel) && (isEmpty(stepUpEntity) || stepUpEntity.level === 'loa1')) ||
        strongAuthenticationFacetValues.indexOf(minimalLoaLevel) > -1 ||
        (stepUpEntity != null && strongAuthenticationFacetValues.indexOf('IDP_' + stepUpEntity.level) > -1)
      )
    },
  }

  strongAuthenticationFacet.values.forEach((value) => {
    value.count = (apps) =>
      apps.filter((app) => {
        const stepUpEntity = stepupEntities.find((e) => e.name === app.spEntityId)
        if (value.searchValue === 'NONE') {
          return isEmpty(app.minimalLoaLevel) && isEmpty(stepUpEntity)
        }
        const idpMatches = stepUpEntity != null && value.searchValue === 'IDP_' + stepUpEntity.level
        const spMatches = value.searchValue === 'SP_' + app.minimalLoaLevel
        return spMatches || idpMatches
      }).length
  })

  facets.push(strongAuthenticationFacet)
  const attributeFacet = {
    name: I18n.t('facets.static.arp.name'),
    tooltip: I18n.t('facets.static.arp.tooltip'),
    searchValue: 'attributes',
    values: arpAttributes.map((attr) => {
      const val = attr.substring(attr.lastIndexOf(':') + 1)
      return { value: val.charAt(0).toUpperCase() + val.slice(1), searchValue: attr }
    }),
    filterApp: function (app) {
      const attrFacetValues = activeFacets['attributes'] || []
      const attributes = Object.keys(app.arp.attributes)
      if ((app.arp.noArp && !app.manipulation) || attrFacetValues.length === 0) {
        return true
      }
      if (app.arp.noAttrArp) {
        return false
      }
      return attrFacetValues.filter((attr) => attributes.indexOf(attr) > -1).length === attrFacetValues.length
    },
  }

  attributeFacet.values.forEach((value) => {
    value.count = (apps) =>
      apps.filter((app) => {
        if (app.arp.noArp && !app.manipulation) {
          return true
        }
        if (app.arp.noArp || app.arp.noAttrArp) {
          return false
        }
        const requiredAttributes = Object.keys(app.arp.attributes)
        return requiredAttributes.indexOf(value.searchValue) > -1
      }).length
  })
  facets.push(attributeFacet)
  const typeConsentFacet = {
    name: I18n.t('facets.static.type_consent.name'),
    tooltip: I18n.t('facets.static.type_consent.tooltip'),
    searchValue: 'type_consent',
    values: consentTypes.map((t) => ({
      searchValue: t,
      value: I18n.t(`facets.static.type_consent.${t.toLowerCase()}`),
    })),
    filterApp: function (app) {
      const consentFacetValues = activeFacets['type_consent'] || []
      const consent = idpDisableConsent.find((dc) => dc.spEntityId === app.spEntityId) || {
        type: 'DEFAULT_CONSENT',
      }
      return consentFacetValues.length === 0 || consentFacetValues.includes(consent.type)
    },
  }
  typeConsentFacet.values.forEach((value) => {
    value.count = (apps) =>
      apps.filter((app) => {
        const consent = idpDisableConsent.find((dc) => dc.spEntityId === app.spEntityId) || {
          type: 'DEFAULT_CONSENT',
        }
        return value.searchValue === consent.type
      }).length
  })
  facets.push(typeConsentFacet)

  if (currentUser.superUser) {
    facets.push({
      name: I18n.t('facets.static.attribute_manipulation.name'),
      searchValue: 'manipulation_notes',
      values: [
        {
          value: I18n.t('facets.static.attribute_manipulation.yes'),
          searchValue: 'yes',
          count: (apps) => apps.filter((app) => !isEmpty(app.manipulationNotes)).length,
        },
        {
          value: I18n.t('facets.static.attribute_manipulation.no'),
          searchValue: 'no',
          count: (apps) => apps.filter((app) => isEmpty(app.manipulationNotes)).length,
        },
      ],
      filterApp: function (app) {
        return filterYesNoFacet('manipulation_notes', app.manipulationNotes)
      },
    })
  }
  if (!currentUser.manageConsentEnabled) {
    facets = facets.filter((facet) => facet.name !== I18n.t('facets.static.type_consent.name'))
  }
  if (currentUser.guest) {
    facets = facets.filter(
      (facet) =>
        facet.name !== I18n.t('facets.static.connection.name') &&
        facet.name !== I18n.t('facets.static.used_by_idp.name')
    )
  }

  const preparedRemoteFacets = remoteFacets.map((facet) => {
    facet.values = facet.values.map((value) => {
      value.count = (apps) =>
        apps.filter((app) => {
          const categories = app.categories.reduce((memo, category) => {
            memo[category.searchValue] = category.values.map((v) => v.value)
            return memo
          }, {})
          const appTags = categories[facet.searchValue] || []
          return appTags.indexOf(value.value) > -1
        }).length

      return value
    })

    facet.filterApp = function (app) {
      const normalizedCategories = app.categories.reduce((memo, category) => {
        memo[category.searchValue] = category.values.map((v) => v.value)
        return memo
      }, {})

      const facetValues = activeFacets[facet.searchValue] || []
      if (facetValues.length > 0) {
        const hits = normalizedCategories[facet.searchValue].filter((facetValue) => {
          return facetValues.indexOf(facetValue) > -1
        })
        if (hits.length === 0) {
          return false
        }
      }
      return true
    }

    return facet
  })

  facets = facets.concat(preparedRemoteFacets)

  // Filter by search query
  const filteredApps = apps
    .filter((app) => {
      return [...Object.values(app.descriptions), ...Object.values(app.names)].some(
        (name) => name.toLowerCase().indexOf(searchQuery) > -1 || app.spEntityId.toLowerCase().indexOf(searchQuery) > -1
      )
    })
    .filter((app) => {
      return facets.every((facet) => {
        if (!activeFacets[facet.searchValue] || activeFacets[facet.searchValue].length === 0) {
          return true
        }
        return facet.filterApp(app)
      })
    })
    .sort((a, b) => a.name.toLowerCase().localeCompare(b.name.toLowerCase()))

  const paginatedApps = filteredApps.slice((page - 1) * PAGE_COUNT, page * PAGE_COUNT)

  return (
    <div className="mod-app-overview">
      <div className="facets-and-table">
        <Facets
          apps={filteredApps}
          currentUser={currentUser}
          facets={facets}
          selectedFacets={activeFacets}
          filteredCount={filteredApps.length}
          totalCount={apps.length}
          onChange={handleFacetChange}
          onReset={resetFilters}
        />
        <div className="apps-and-search">
          <div className="top-bar">
            <h2>{I18n.t('apps.overview.connected_services')}</h2>
            <div className="search-container">
              <input
                type="search"
                value={searchQuery}
                onChange={onSearch}
                placeholder={I18n.t('apps.overview.search_hint')}
              />
              <FontAwesomeIcon icon={faSearch} />
            </div>
          </div>
          <table className="apps-table">
            <thead>
              <tr>
                <th>&nbsp;</th>
                <th>{I18n.t('apps.overview.name')}</th>
                <th>{I18n.t('apps.overview.organisation')}</th>
                <th>{I18n.t('apps.overview.licenseStatus')}</th>
              </tr>
            </thead>
            <tbody>
              {paginatedApps.map((app) => {
                return (
                  <tr key={app.id}>
                    <td className="connected">{app.connected && <ConnectedServiceIcon />}</td>
                    <td className="name">
                      <Link to="/">{app.name}</Link>
                    </td>
                    <td className="vendor">{app.organisation}</td>
                    <td className="license">{I18n.t('facets.static.license.' + app.licenseStatus.toLowerCase())}</td>
                  </tr>
                )
              })}
            </tbody>
          </table>
          <Pagination page={page} pageCount={PAGE_COUNT} total={filteredApps.length} onChange={setPage} />
        </div>
      </div>
    </div>
  )
}
