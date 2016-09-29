/* eslint-disable */

const config = require("./webpack.config");
const webpack = require("webpack");

delete config.devtool;
config.plugins.push(new webpack.DefinePlugin({
  "process.env": {
    "NODE_ENV": JSON.stringify("production")
  }
}));

config.plugins.push(new webpack.optimize.DedupePlugin());
config.plugins.push(new webpack.optimize.UglifyJsPlugin());
config.plugins.push(new webpack.optimize.AggressiveMergingPlugin());

module.exports = config;
