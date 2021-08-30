#!/bin/bash
rm -Rf build/*
rm -Rf target/*
yarn test && yarn install
echo "BUILDING CSS"
yarn build-css
echo "BUILDING JS"
yarn build
