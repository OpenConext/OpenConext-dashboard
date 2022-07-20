#!/bin/bash
rm -Rf build/*
rm -Rf target/*
source $NVM_DIR/nvm.sh
nvm use
yarn install --force
yarn test
echo "BUILDING CSS"
yarn build-css
echo "BUILDING JS"
yarn build
