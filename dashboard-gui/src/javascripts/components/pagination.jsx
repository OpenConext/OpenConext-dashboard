import React from 'react'

import pagination from '../utils/pagination'

export default function Pagination({ page, total, pageCount = 20, onChange }) {
  if (total <= pageCount) {
    return null
  }
  const nbrPages = Math.ceil(total / pageCount)
  const rangeWithDots = pagination(page, nbrPages)
  return (
    <section className="pagination">
      <section className="pagination-container">
        {nbrPages > 1 && page !== 1 && (
          <button type="button" onClick={() => onChange(page - 1)} title="Previous page" aria-label="Previous page">
            <i className="fa fa-arrow-left"></i>
          </button>
        )}
        {rangeWithDots.map((nbr, index) =>
          typeof nbr === 'string' || nbr instanceof String ? (
            <span key={index} className="dots">
              {nbr}
            </span>
          ) : nbr === page ? (
            <span className="current" key={index}>
              {nbr}
            </span>
          ) : (
            <button key={index} type="button" onClick={() => onChange(nbr)}>
              {nbr}
            </button>
          )
        )}
        {nbrPages > 1 && page !== nbrPages && (
          <button type="button" onClick={() => onChange(page + 1)} title="Next page" aria-label="Next page">
            <i className="fa fa-arrow-right"></i>
          </button>
        )}
      </section>
    </section>
  )
}
