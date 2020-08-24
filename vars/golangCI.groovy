import local.domain.*

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // podTemplate configuration for the container running the below stages
    podTemplate(
    cloud: 'kubernetes',
    namespace: 'jenkins',
    activeDeadlineSeconds: '300',
    showRawYaml: 'false',
    runAsUser: "0",
    runAsGroup: "1000",
    containers: [
        containerTemplate(
            name: 'centos',
            image: 'centos:centos7',
            ttyEnabled: true,
            privileged: true,
            command: '/usr/sbin/init'
        )
    ]) {

        try {
            node(POD_LABEL) {
/*
                // Ensure imported classes are null
                Docker docker = null

                stage("Clean Workspace") {
                    cleanWs()
                } // stage end

                stage("Create Dependencies") {
                    docker = new Docker(this)
                } // stage end

                stage("Run Docker Class") {
                    docker.test("${config.message}")
                } // stage end
*/                
                stage("Get Golang Project") {
                    git url: 'https://github.com/Condoamanti/programming.git'
                } // stage end

                container("centos") {
                    stage("Remove Default CentOS7 Repositories") {
                    sh """
                    rm -rf /etc/yum.repos.d/*
                    """
                    } // stage end

                    stage("Setup Artifactory Repository") {
                        sh """
                        echo \"# Artifactory Repository
[artifactory-centos-7-os]
name=artifactory-centos-7-os
baseurl=http://admin:AP72goB1ugbhNixgk4oZQD1JSMK@k8snode1dc1.jittersolutions.com:32382/artifactory/rpm/7/os/x86_64
enabled=1
gpgcheck=0

[artifactory-centos-7-extras]
name=artifactory-centos-7-extras
baseurl=http://admin:AP72goB1ugbhNixgk4oZQD1JSMK@k8snode1dc1.jittersolutions.com:32382/artifactory/rpm/7/extras/x86_64
enabled=1
gpgcheck=0

[artifactory-centos-7-fasttrack]
name=artifactory-centos-7-fasttrack
baseurl=http://admin:AP72goB1ugbhNixgk4oZQD1JSMK@k8snode1dc1.jittersolutions.com:32382/artifactory/rpm/7/fasttrack/x86_64
enabled=1
gpgcheck=0

[artifactory-centos-7-updates]
name=artifactory-centos-7-updates
baseurl=http://admin:AP72goB1ugbhNixgk4oZQD1JSMK@k8snode1dc1.jittersolutions.com:32382/artifactory/rpm/7/updates/x86_64
enabled=1
gpgcheck=0

[artifactory-centos-7-cr]
name=artifactory-centos-7-cr
baseurl=http://admin:AP72goB1ugbhNixgk4oZQD1JSMK@k8snode1dc1.jittersolutions.com:32382/artifactory/rpm/7/cr/x86_64
enabled=1
gpgcheck=0\" >> /etc/yum.repos.d/artifactory.repo 2>&1
"""
                    } // stage end

                    stage("Download Docker Dependencies") {
                    sh """
                    yum update --assumeyes --quiet
                    yum install --assumeyes --quiet container-selinux
                    yum install --assumeyes --quiet yum-utils
                    yum clean all
                    """
                    } // stage end

                    stage("Download Docker") {
                    sh """
                    yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
                    yum install --assumeyes --quiet docker-ce docker-ce-cli containerd.io
                    yum clean all

                    touch /var/run/docker.sock
                    chown root:docker /var/run/docker.sock
                    usermod -aG docker root

                    echo \"###########################\"
                    systemctl start docker
                    systemctl status docker
                    """
                    } // stage end

                    stage("Build Docker Image") {
                    sh """
                    docker build -t condoamanti/dockergo ./golang/web
                    ls ./golang/web
                    """
                    } // stage end
                } // container end
            } // node end
        } catch (e) {
            echo "Exception: ${e}"
            currentBuild.result = 'FAILURE'
        }
    } // podTemplate end
} // call(body) end