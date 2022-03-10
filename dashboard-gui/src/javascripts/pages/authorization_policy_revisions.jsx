import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import moment from 'moment'
import I18n from 'i18n-js'
import { getPolicyRevisions } from '../api'
import RevisionModal from '../components/revision_modal'

export default function AuthorizationPolicyRevisions({ app, type }) {
  const [revisions, setRevisions] = useState([])
  const [current, setCurrent] = useState(null)
  const params = useParams()

  async function fetchRevisions() {
    const res = await getPolicyRevisions(params.policyId)
    setRevisions(res.payload.sort((rev1, rev2) => rev2.revisionNbr - rev1.revisionNbr))
  }

  useEffect(() => {
    fetchRevisions()
  }, [])

  console.log(revisions)
  console.log(current)
  function createdDate(revision) {
    if (revision.created) {
      const created = moment.unix(revision.created / 1000)
      created.locale(I18n.locale)
      return created.format('LLLL')
    }
    return ''
  }

  function getPrevious() {
    if (!current) {
      return null
    }

    return revisions.find((revision) => revision.revisionNbr === current.revisionNbr - 1)
  }

  const previous = getPrevious()

  return (
    <div className="policy-revisions">
      <h2>{I18n.t('revisions.title')}</h2>
      <p>{I18n.t('revisions.intro_1')}</p>
      <p>{I18n.t('revisions.intro_2')}</p>
      <div className="revisions">
        {revisions.map((revision, index) => (
          <div className="revision" key={index}>
            <div className="revision-details">
              <h3>{revision.name}</h3>
              <p>{I18n.t('revisions.revision') + ' ' + revision.revisionNbr}</p>
              <p>
                {I18n.t('policy_detail.sub_title', {
                  displayName: revision.userDisplayName,
                  created: createdDate(revision),
                })}
              </p>
            </div>
            <div className="revision-actions">
              <button className="c-button" type="button" onClick={() => setCurrent(revision)}>
                &lt; &gt;
              </button>
            </div>
          </div>
        ))}
      </div>
      <Link
        className="c-button white"
        type="button"
        onClick={() => setCurrent(null)}
        to={`/apps/${app.id}/${type}/settings/authorization_policies`}
      >
        {I18n.t('apps.detail.back')}
      </Link>
      <RevisionModal isOpen={!!current} current={current} previous={previous} onClose={() => setCurrent(null)} />
    </div>
  )
}
