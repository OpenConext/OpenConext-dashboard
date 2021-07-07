import React from 'react'
import en from '../../locale/en'
import nl from '../../locale/nl'
import pt from '../../locale/pt'

import start from '../base'
import I18n from 'i18n-js'

start()

expect.extend({
  toContainKey(translation, key) {
    return {
      message: () => `Expected ${key} to be present in ${JSON.stringify(translation)}`,
      pass: translation !== undefined && translation[key] !== undefined,
    }
  },
})

test('All translations exists in EN, PT and NL', () => {
  const contains = (translation, translationToVerify) => {
    Object.keys(translation).forEach((key) => {
      expect(translationToVerify).toContainKey(key)
      const value = translation[key]
      if (typeof value === 'object') {
        contains(value, translationToVerify[key])
      }
    })
  }
  contains(I18n.translations.en, I18n.translations.nl)
  contains(I18n.translations.nl, I18n.translations.en)
  contains(I18n.translations.pt, I18n.translations.pt)
})
