#!/bin/bash
rm -Rf build/*
rm -Rf target/*
yarn install && yarn build