package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class Unity3DContext implements Context {
    String unity3dName
    String returnCodes
    final List<String> args = []

    /**
     * Specifies the name of the Unity3D installation to be used for this build step.
     */
    void unity3dInstallation(String unity3dInstallation) {
        this.unity3dName = unity3dInstallation
    }

    /**
     * Specifies the command line return codes that should result in unstable builds instead of failures.
     */
    void returnCodes(String returnCodes) {
        this.returnCodes = returnCodes
    }

    /**
     * Specifies command line arguments to be added to the unity3d call. Can be called multiple times to add more args.
     */
    void args(String args) {
        this.args << args
    }
}
