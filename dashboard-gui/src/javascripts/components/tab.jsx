import React from 'react'
import { Link } from 'react-router-dom'

export default function Tab(props) {
  return (
    <Link className={`tab ${props.active ? 'active' : ''}`} to={props.to}>
      {props.children}
    </Link>
  )
}
