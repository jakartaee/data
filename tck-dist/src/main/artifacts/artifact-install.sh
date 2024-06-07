#!/usr/bin/env bash

## A sample script to install the artifact directory contents into a local maven repository

POM=$(ls *.pom)
TCK=$(ls *.jar | grep -v sources)

NO_EXT=${POM%.*}      # jakarta.data-parent-1.0.0-SNAPSHOT.pom > jakarta.data-parent-1.0.0-SNAPSHOT
NO_REPO=${NO_EXT#*-}  # jakarta.data-parent-1.0.0-SNAPSHOT > parent-1.0.0-SNAPSHOT
VERSION=${NO_REPO#*-} # parent-1.0.0-SNAPSHOT > 1.0.0-SNAPSHOT

echo "Installing $POM with version $VERSION"
mvn org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file \
-Dfile=$POM \
-DgroupId=jakarta.data \
-DartifactId=jakarta.data-parent \
-Dversion="$VERSION" \
-Dpackaging=pom

echo "Installing $TCK"
mvn org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file \
-Dfile=$TCK
