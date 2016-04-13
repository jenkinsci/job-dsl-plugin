job('example') {
    steps {
        unity3d {
            unity3dInstallation('Unity 5.1')
            returnCodes('2,3')
            args('-batchmode')
            args('-executeMethod')
        }
    }
}
