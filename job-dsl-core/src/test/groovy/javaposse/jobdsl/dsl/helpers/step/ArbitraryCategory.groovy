package javaposse.jobdsl.dsl.helpers.step

class ArbitraryCategory {
    def sayHello(String who) {
        "Hi $who !"
    }

    static useJobManagement(StepContext self, others) {
        self.stepNodes << new NodeBuilder().'credentials' {
            id(self.jobManagement.getCredentialsId(others[0]))
        }
    }
}
