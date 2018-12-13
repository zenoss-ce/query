#!/usr/bin/env groovy

node {

  stage('Checkout') {
    checkout scm
  }

  stage('Build') {
    docker.image('zenoss/build-tools:0.0.10').inside() { 
      withMaven(mavenSettingsConfig: 'bintray') {
        sh '''
          touch .checkedenv .checkedtools .checkedtools_version_brand
          export PATH=$MVN_CMD_DIR:$PATH 
          make build
        '''
      }
    }
  }

  stage('Publish app') {
    def remote = [:]
    withFolderProperties {
      withCredentials( [sshUserPrivateKey(credentialsId: 'PUBLISH_SSH_KEY', keyFileVariable: 'identity', passphraseVariable: '', usernameVariable: 'userName')] ) {
        remote.name = env.PUBLISH_SSH_HOST
        remote.host = env.PUBLISH_SSH_HOST
        remote.user = userName
        remote.identityFile = identity
        remote.allowAnyHosts = true

        def tar_ver = sh( returnStdout: true, script: "awk -F'(>|<)' '/artifact.+central-query-parent/{getline; print \$3}' pom.xml" ).trim()
        sshPut remote: remote, from: 'central-query/target/central-query-' + tar_ver + '-zapp.tar.gz', into: env.PUBLISH_SSH_DIR
      }
    }
  }
}
