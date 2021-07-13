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
          <i
            className="fa fa-arrow-left"
            role="button"
            onClick={() => onChange(page - 1)}
            tabIndex={0}
            onKeyDown={() => onChange(page - 1)}
          ></i>
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
            <span key={index} role="button" tabIndex={0} onClick={() => onChange(nbr)} onKeyDown={() => onChange(nbr)}>
              {nbr}
            </span>
          )
        )}
        {nbrPages > 1 && page !== nbrPages && (
          <i
            className="fa fa-arrow-right"
            role="button"
            tabIndex={0}
            onClick={() => onChange(page + 1)}
            onKeyDown={() => onChange(page + 1)}
          ></i>
        )}
      </section>
    </section>
  )
}
