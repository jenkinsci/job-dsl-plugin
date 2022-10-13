package support;

class Helper {
    static void extend(def job) {
        job.with {
            wrappers {
                release {
                    preBuildSteps {
                        shell 'echo 1'
                    }
                    postBuildSteps {
                        shell 'echo 2'
                    }
                }
            }
        }
    }
}