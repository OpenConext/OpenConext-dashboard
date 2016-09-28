module.exports = {
  "env": {
    "node": false,
    "browser": true,
    "es6": true,
    "commonjs": true
  },
  "globals": {
    "fetch": false,
    "_": false
  },
  "parser": "babel-eslint",
  "parserOptions": {
    "ecmaFeatures": {
      "jsx": true
    }
  },
  "plugins": [
    "react"
  ],
  "extends": ["eslint:recommended", "plugin:react/recommended"],
  "rules": {
    "block-scoped-var": "error",
    "consistent-return": "error",
    "eqeqeq": [
      "error",
      "smart"
    ],
    "guard-for-in": "error",
    "indent": [
      "error",
      2
    ],
    "linebreak-style": [
      "error",
      "unix"
    ],
    "no-console": "error",
    "no-octal-escape": "error",
    "no-param-reassign": "error",
    "no-var": "error",
    "prefer-const": [
      "error",
      {"destructuring": "all"}
    ],
    "quotes": [
      "error",
      "double"
    ],
    "semi": [
      "error",
      "always"
    ]
  }
};
