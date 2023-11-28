#!/usr/bin/env bash

## A sample script to install the artifact directory contents into a local maven repository

if [[ $1 =~ ^[0-9]+\.[0-9]+\.[0-9]+.*$ ]]; then
  VERSION="$1"
else
  echo "A valid version is required to execute this script"
  exit 1
fi

# Parent pom
mvn org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file \
-Dfile=jakarta.data-parent-"$VERSION".pom \
-DgroupId=jakarta.data \
-DartifactId=jakarta.data-parent \
-Dversion="$VERSION" \
-Dpackaging=pom

# Jakarta Data TCK Installed Library
mvn org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file \
-Dfile=jakarta-data-tck-"$VERSION".jar
