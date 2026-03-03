pipeline {
    agent any

    environment {
        REGISTRY = "brouse13"
        IMAGE_TAG = "${BUILD_NUMBER}"
        KUBE_CONFIG = credentials('kubeconfig-id')
    }

    tools {
		maven 'Maven 3.8.8'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Maven Project') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    def modules = ["lottery-client", "lottery-server", "lottery-dns", "lottery-seller"]

                    for (module in modules) {
                        sh """
                        docker build -t $REGISTRY/${module}:${IMAGE_TAG} ./${module}
                        """
                    }
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    docker.withRegistry('', 'dockerhub-credentials-id') {
                        def modules = ["lottery-client", "lottery-server", "lottery-dns", "lottery-seller"]

                        for (module in modules) {
                            sh "docker push $REGISTRY/${module}:${IMAGE_TAG}"
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    def modules = ["lottery-client", "lottery-server", "lottery-dns", "lottery-seller"]

                    for (module in modules) {
                        sh """
                        kubectl set image deployment/${module} \
                        ${module}=$REGISTRY/${module}:${IMAGE_TAG} \
                        --kubeconfig=$KUBE_CONFIG
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Deployment successful"
        }
        failure {
            echo "Build failed"
        }
    }
}