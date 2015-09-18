job('example-1') {
    wrappers {
        buildInDocker {
            image('centos:7')
            volume('/dev/urandom', '/dev/random')
            verbose()
        }
    }
}

job('example-2') {
    wrappers {
        buildInDocker {
            dockerfile()
            volume('/dev/urandom', '/dev/random')
            verbose()
        }
    }
}
