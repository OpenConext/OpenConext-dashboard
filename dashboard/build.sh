#!/bin/bash
echo "Build already?"
rm -Rf dist/*
yarn install
yarn run webpack
