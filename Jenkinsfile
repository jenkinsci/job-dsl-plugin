// Only keep the 10 most recent builds.
properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '10']]])

node('docker') {
  stage('Checkout') {
    checkout scm
  }

  stage('Build') {
    docker.image('openjdk:7-jdk').inside {
      timeout(60) {
        sh './gradlew --no-daemon build'
      }
    }
  }

  stage('Results') {
    junit '**/build/test-results/**/TEST-*.xml'
    archiveArtifacts artifacts: 'job-dsl-plugin/build/libs/*.hpi, **/build/reports/**'
  }
}
