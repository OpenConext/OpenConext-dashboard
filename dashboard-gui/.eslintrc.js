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
  },
}
