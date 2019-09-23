pipeline {
  agent any
  stages {
    stage('启动前准备') {
      steps {
        echo 'remove image'
        // 停止容器
        sh 'docker stop simple-beauty'
        // 删除容器
        sh 'docker rm simple-beauty'
        // 删除镜像
        sh 'docker rmi registry.cn-hangzhou.aliyuncs.com/jie12366/simple-beauty:1.0'
      }
    }
    stage('获取镜像') {
      steps {
        echo 'get image'
        sh 'docker login --username=熊义杰的docker registry.cn-hangzhou.aliyuncs.com --password=LOVEjie@1111'
        sh 'docker pull registry.cn-hangzhou.aliyuncs.com/jie12366/simple-beauty:1.0'
      }
    }
    stage('启动服务') {
      steps {
        echo 'start simple-beauty'
        sh 'docker run -d -p 81:81 --name simple-beauty registry.cn-hangzhou.aliyuncs.com/jie12366/simple-beauty:1.0'
      }
    }
  }
}