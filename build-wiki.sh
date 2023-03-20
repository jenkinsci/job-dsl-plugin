#!/bin/sh -eux

mkdir -p target/docs

mvn -B -e -ntp -q -Dexpression=jenkins.version -Doutput=jenkins.version help:evaluate
JENKINS_VERSION=`cat jenkins.version`

curl -sSO https://repo.jenkins-ci.org/releases/org/jenkins-ci/plugins/job-dsl/maven-metadata.xml
VERSION=$(grep '<latest>.*</latest>' maven-metadata.xml | cut -d'>' -f2 | cut -d'<' -f1)

cp -r docs target
sed -i -e "s/@jenkinsVersion@/${JENKINS_VERSION}/g" target/docs/*.md
sed -i -e "s/@version@/${VERSION}/g" target/docs/*.md

exit 0
