import React from 'react'
import PropTypes from 'prop-types'
import I18n from 'i18n-js'

class Screenshots extends React.Component {
  constructor() {
    super()

    this.state = {
      open: false,
    }
  }

  render() {
    let screenshotsUrls = this.props.screenshotUrls
    if (!screenshotsUrls) {
      screenshotsUrls = []
    }
    return (
      <div className="mod-screenshots">
        {screenshotsUrls.map(this.renderScreenshotThumbnail.bind(this))}
        {this.renderOpenScreenshot()}
      </div>
    )
  }

  renderScreenshotThumbnail(screenshot, index) {
    return (
      <a key={index} className="screenshot-thumb" href="/show" onClick={this.showScreenshot(index)}>
        <img alt="" src={screenshot} />
      </a>
    )
  }

  showScreenshot(index) {
    return function (event) {
      event.preventDefault()
      event.stopPropagation()
      this.setState({ open: true, index: index })
    }.bind(this)
  }

  renderOpenScreenshot() {
    if (this.state.open) {
      return (
        <div className="lightbox-overlay">
          <a href="/close" className="close-btn" onClick={this.closeScreenshot.bind(this)}>
            {I18n.t('apps.detail.close_screenshot')}
          </a>
          <a href="/close" onClick={this.closeScreenshot.bind(this)}>
            <img alt="" src={this.props.screenshotUrls[this.state.index]} />
          </a>
        </div>
      )
    }

    return null
  }

  closeScreenshot(event) {
    event.preventDefault()
    event.stopPropagation()
    this.setState({ open: false })
  }
}

Screenshots.propTypes = {
  screenshotUrls: PropTypes.arrayOf(PropTypes.string),
}

export default Screenshots
