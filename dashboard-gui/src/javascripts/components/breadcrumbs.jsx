import React from 'react'
import { Link } from 'react-router-dom'

export default function Breadcrumbs({ items }) {
  return (
    <div className="mod-breadcrumbs">
      <div className="container">
        {items.map((item, index) => {
          const active = index == items.length - 1
          return (
            <Link key={item.link} to={item.link} className={active ? 'active' : ''}>
              {item.text}
            </Link>
          )
        })}
      </div>
    </div>
  )
}
