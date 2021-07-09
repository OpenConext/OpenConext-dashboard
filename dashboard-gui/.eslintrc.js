module.exports = {
  env: {
    node: false,
    browser: true,
    es6: true,
    commonjs: true,
  },
  globals: {
    fetch: false,
    _: false,
    SurfCharts: false,
    expect: false,
    test: false,
  },
  parser: 'babel-eslint',
  parserOptions: {
    ecmaFeatures: {
      jsx: true,
    },
  },
  plugins: ['react', 'jsx-a11y', 'prettier'],
  extends: ['eslint:recommended', 'plugin:react/recommended', 'plugin:jsx-a11y/recommended', 'prettier'],
  settings: {
    react: {
      version: 'detect',
    },
  },
  rules: {
    'react/prop-types': 0,
    'prettier/prettier': 'error',
    'jsx-a11y/click-events-have-key-events': 'warn',
    'jsx-a11y/no-static-element-interactions': 'warn',
    'react/no-unescaped-entities': 'warn',
    'react/no-find-dom-node': 'warn',
    'react/no-string-refs': 'warn',
  },
}
