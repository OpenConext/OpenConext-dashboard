import React, { useEffect, useState } from 'react'
import I18n from 'i18n-js'
import ReactTooltip from 'react-tooltip'
import qs from 'query-string'
import includes from 'lodash.includes'
import isEmpty from 'lodash.isempty'
import {disableConsent, exportApps, getPolicies} from '../api'
import stopEvent from '../utils/stop'
import { Link, useHistory, useLocation } from 'react-router-dom'
import { ReactComponent as ConnectedServiceIcon } from '../../images/tags-favorite-star.svg'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faSearch } from '@fortawesome/free-solid-svg-icons'
import { setBackPath } from '../utils/back_path'

import Facets from '../components/facets'
import Pagination from '../components/pagination'
import { consentTypes } from '../utils/utils'

const PAGE_COUNT = 25

export default function AppList({ apps, currentUser, facets: remoteFacets, connected }) {
  const history = useHistory()
  const location = useLocation()
  const queryString = qs.parse(location.search)
  const [searchQuery, setSearchQuery] = useState(queryString.search || '')
  const [activeFacets, setActiveFacets] = useState(queryString.activeFacets ? JSON.parse(queryString.activeFacets) : {})
  const [downloading, setDownloading] = useState(false)
  const [page, setPage] = useState(parseInt(queryString.page) || 1)
  const [entityCategoriesFacetSelector, setEntityCategoriesFacetSelector] = useState(false)
  const [idpDisableConsent, setIdpDisableConsent] = useState([])
  const [policies, setPolicies] = useState([])


  async function fetchDisableConsent() {
    const value = await disableConsent()
    const allPolicies = await getPolicies()
    setIdpDisableConsent(value)
    setPolicies(allPolicies.payload)
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
    history.replace({
      search: qs.stringify({ ...qs.parse(location.search), activeFacets: JSON.stringify(newFacets), page: 1 }),
    })
  }

  function onPageChange(page) {
    setPage(page)
    history.replace({ search: qs.stringify({ ...qs.parse(location.search), page }) })
  }

  function resetFilters() {
    setActiveFacets({})
    setSearchQuery('')
    setPage(1)
    history.replace({ search: null })
  }

  function onSearch(e) {
    setSearchQuery(e.target.value.toLowerCase())
    setPage(1)
    history.replace({
      search: qs.stringify({ ...qs.parse(location.search), search: e.target.value.toLowerCase(), page: 1 }),
    })
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

  if (!connected) {
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
  }
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
    name: I18n.t('facets.static.authorization_rules.name'),
    tooltip: I18n.t('facets.static.authorization_rules.tooltip'),
    searchValue: 'authorization_rules',
    values: [
      {
        value: I18n.t('facets.static.authorization_rules.yes'),
        searchValue: 'yes',
        count: (apps) => apps.filter((app) => policies.some(policy => policy.serviceProviderIds.includes(app.spEntityId)
            && policy.identityProviderIds.includes(currentUser.getCurrentIdp().id))).length,
      },
      {
        value: I18n.t('facets.static.authorization_rules.no'),
        searchValue: 'no',
        count: (apps) => apps.filter((app) => !policies.some(policy => policy.serviceProviderIds.includes(app.spEntityId)
            && policy.identityProviderIds.includes(currentUser.getCurrentIdp().id))).length,
      },
    ],
    filterApp: function (app) {
      return filterYesNoFacet('authorization_rules', policies.some(policy => policy.serviceProviderIds.includes(app.spEntityId)
          && policy.identityProviderIds.includes(currentUser.getCurrentIdp().id)))
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
        value: I18n.t('facets.static.entity_category.code_of_conduct2'),
        searchValue: 'https://refeds.org/category/code-of-conduct/v2',
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
            title="select all"
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
  if (!currentUser.guest) {
    facets.push(strongAuthenticationFacet)
    const mfaEntities = currentUser.currentIdp.mfaEntities
    const mfaLevels = [
      'https://refeds.org/profile/mfa',
      'http://schemas.microsoft.com/claims/multipleauthn'
    ]
    facets.push({
      name: I18n.t('facets.static.mfa.name'),
      tooltip: I18n.t('facets.static.mfa.tooltip'),
      searchValue: 'mfa',
      values: [
        {
          value: I18n.t('mfa_panel.mfa_short'),
          searchValue: mfaLevels[0],
          count: apps => apps.filter(app => mfaEntities.some(entity => entity.name === app.spEntityId &&
              entity.level === mfaLevels[0])).length
        },
        {
          value: I18n.t('mfa_panel.multipleauthn_short'),
          searchValue: mfaLevels[1],
          count: apps => apps.filter(app => mfaEntities.some(entity => entity.name === app.spEntityId &&
              entity.level === mfaLevels[1])).length
        },
        {
          value: I18n.t('facets.static.mfa.other'),
          searchValue: 'OTHER',
          count: apps => apps.filter(app => {
            const hit = mfaEntities.find(entity => entity.name === app.spEntityId)
            return hit && mfaLevels.indexOf(hit.level) === -1
          }).length
        },
        {
          value: I18n.t('facets.static.mfa.none'),
          searchValue: 'NONE',
          count: apps => apps.filter(app => !mfaEntities.some(entity => entity.name === app.spEntityId)).length
        }
      ],
      filterApp: function (app) {
        const mfaFacetValues = activeFacets['mfa'] || []
        if (mfaFacetValues.indexOf('OTHER') > -1) {
          const hit = mfaEntities.find(entity => entity.name === app.spEntityId)
          return hit && mfaLevels.indexOf(hit.level) === -1
        }
        if (mfaFacetValues.indexOf('NONE') > -1) {
          return !mfaEntities.some(entity => entity.name === app.spEntityId)
        }
        return mfaFacetValues.length === 0 || mfaEntities.some(entity => entity.name === app.spEntityId &&
            mfaFacetValues.indexOf(entity.level) > -1)
      }.bind(this),
    })
  }

  const attributeFacet = {
    name: I18n.t('facets.static.arp.name'),
    tooltip: I18n.t('facets.static.arp.tooltip'),
    searchValue: 'attributes',
    values: arpAttributes.map((attr) => {
      const val = attr.substring(attr.lastIndexOf(':') + 1)
      return { value: val, searchValue: attr }
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
  if (!currentUser.guest) {
    facets.push(typeConsentFacet)
  }


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
        facet.name !== I18n.t('facets.static.used_by_idp.name') &&
        facet.name !== I18n.t('facets.static.authorization_rules.name')
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
  const searchQueryLower = searchQuery.toLowerCase().trim();
  const searchQueryList = searchQueryLower.split(" ");
  const filteredApps = apps
    .filter((app) => {
      return [...Object.values(app.descriptions), ...Object.values(app.names)]
          .filter(name => !isEmpty(name))
          .some(name => searchQueryList.every(q => name.toLowerCase().indexOf(q) > -1)  ||
              app.spEntityId.toLowerCase().indexOf(searchQueryLower) > -1
      )
    })
    .filter((app) => {
      return facets.every((facet) => {
        if (facet.searchValue === 'entity_category' && entityCategoriesFacetSelector) {
          return facet.filterApp(app)
        }

        if (!activeFacets[facet.searchValue] || activeFacets[facet.searchValue].length === 0) {
          return true
        }
        return facet.filterApp(app)
      })
    })
    .sort((a, b) => a.name.toLowerCase().localeCompare(b.name.toLowerCase()))

  const paginatedApps = filteredApps.slice((page - 1) * PAGE_COUNT, page * PAGE_COUNT)

  function fakeClick(obj) {
    const ev = document.createEvent('MouseEvents')
    ev.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null)
    obj.dispatchEvent(ev)
  }

  function handleDownload(e) {
    stopEvent(e)
    if (downloading) {
      return
    }

    setDownloading(true)
    const ids = filteredApps.map((app) => app.id)
    exportApps(currentUser.getCurrentIdpId(), ids).then((res) => {
      const urlObject = window.URL || window.webkitURL || window
      const lines = res.reduce((acc, arr) => {
        acc.push(arr.join(','))
        return acc
      }, [])
      const csvContent = lines.join('\n')
      const export_blob = new Blob([csvContent])
      if ('msSaveBlob' in window.navigator) {
        window.navigator.msSaveBlob(export_blob, 'services.csv')
      } else if ('download' in HTMLAnchorElement.prototype) {
        const save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a')
        save_link.href = urlObject.createObjectURL(export_blob)
        save_link.download = 'services.csv'
        fakeClick(save_link)
      }

      setDownloading(false)
    })
  }

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
            <h1>{connected ? I18n.t('apps.overview.connected_services') : I18n.t('apps.overview.all_services')}</h1>
            <form role="search" className="search-container" aria-controls="apps-table">
              <input
                type="search"
                title="search services"
                value={searchQuery}
                onChange={onSearch}
                placeholder={I18n.t('apps.overview.search_hint')}
              />
              <FontAwesomeIcon icon={faSearch} />
            </form>

            {('msSaveBlob' in window.navigator || 'download' in HTMLAnchorElement.prototype) && (
              <button
                type="button"
                onClick={handleDownload}
                className="c-button white export"
                disabled={filteredApps.length <= 0}
              >
                {I18n.t('facets.download')}
              </button>
            )}
          </div>
          <table className="apps-table" id="apps-table" aria-live="polite">
            <thead>
              <tr>
                {!currentUser.guest && <th scope="col">&nbsp;</th>}
                <th scope="col">{I18n.t('apps.overview.name')}</th>
                <th scope="col">{I18n.t('apps.overview.organisation')}</th>
              </tr>
            </thead>
            <tbody>
              {paginatedApps.map((app, index) => {
                return (
                  <tr key={`${app.id}-${index}`}>
                    {!currentUser.guest && (
                      <td className="connected">
                        {app.connected && <ConnectedServiceIcon focusable title="Connected" />}
                      </td>
                    )}
                    <td className="name">
                      <Link
                        to={`/apps/${app.id}/${app.entityType}/about`}
                        onClick={() => setBackPath(`${location.pathname}${location.search}`)}
                      >
                        {app.name}
                      </Link>
                    </td>
                    <td className="vendor">{app.organisation}</td>
                  </tr>
                )
              })}
              {paginatedApps.length === 0 && (
                <tr key="none">
                  {!currentUser.guest && <td></td>}
                  <td colSpan={2}>{I18n.t('apps.overview.no_results')}</td>
                </tr>
              )}
            </tbody>
          </table>
          <Pagination page={page} pageCount={PAGE_COUNT} total={filteredApps.length} onChange={onPageChange} />
        </div>
      </div>
    </div>
  )
}
