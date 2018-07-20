properties properties: [
  [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '30', numToKeepStr: '10']],
  disableConcurrentBuilds()
]

@Library('holisticon-build-library')
def maven = new de.holisticon.ci.jenkins.Maven()
def utils = new de.holisticon.ci.jenkins.Utils()

timeout(30) {
  node('docker-office') {

    def buildNumber = env.BUILD_NUMBER
    def workspace = env.WORKSPACE
    def buildUrl = env.BUILD_URL

    try {

      // PRINT ENVIRONMENT TO JOB
      echo "received properties: "
      echo "      workspace directory is $workspace"
      echo "      build URL is $buildUrl"
      echo "      build Number is $buildNumber"
      echo "      PATH is $env.PATH"

      stage('Checkout') {
        checkout scm
      }

      stage('Build Docker Image') {
        // build docker images
        sh "./mvnw -Pdocker,frontend"
      }

      stage('Start Docker Image') {
        // run images
        sh "./docker-run.sh"
        sh "echo Waiting for containers to come up"
        utils.waitForAppToBeReady('localhost:18080')
        // send deploy event to logstash
        logstashSend failBuild: false, maxLines: 1000
      }

    } catch (err) {
      throw err
    }
  }
}
