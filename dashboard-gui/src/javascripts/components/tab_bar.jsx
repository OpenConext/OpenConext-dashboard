import React from 'react'

export default function TabBar(props) {
  return (
    <div className="mod-tabs">
      <div className="container">{props.children}</div>
    </div>
  )
}
