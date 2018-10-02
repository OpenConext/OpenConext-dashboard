#!/bin/bash
rm -Rf build/*
rm -Rf target/*
yarn test && yarn install && yarn build