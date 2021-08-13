import React from 'react'

import { emitter, getFlash } from '../utils/flash'
import stopEvent from '../utils/stop'
import PropTypes from 'prop-types'
import { isEmpty } from '../utils/utils'

class Flash extends React.Component {
  constructor() {
    super()

    this.state = {
      flash: null,
    }

    this.callback = (flash) => this.setState({ flash: flash })
  }

  componentDidMount() {
    this.setState({ flash: getFlash() })
    emitter.addListener('flash', this.callback)
  }

  componentWillUnmount() {
    emitter.removeListener('flash', this.callback)
  }

  closeFlash(e) {
    stopEvent(e)
    this.setState({ flash: null })
  }

  render() {
    const { flash } = this.state
    const { className = 'flash' } = this.props
    if (!isEmpty(flash) && !isEmpty(flash.message)) {
      return (
        <div className={`${className} ${flash.type}`}>
          <div className="container">
            <p dangerouslySetInnerHTML={{ __html: flash.message }}></p>
            <a className="close" href="/close" onClick={(e) => this.closeFlash(e)}>
              <i className="fa fa-remove"></i>
            </a>
          </div>
        </div>
      )
    }
    return null
  }
}
Flash.propTypes = {
  className: PropTypes.string,
}

export default Flash
