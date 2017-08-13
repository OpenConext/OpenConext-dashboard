#!/bin/bash
rm -Rf dist/*
yarn install
yarn run webpack
