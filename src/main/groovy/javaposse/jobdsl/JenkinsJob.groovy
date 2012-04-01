package javaposse.jobdsl

class JenkinsJob {

    Node job = null

    def using(String templateName) {
        job = new XmlParser().parse(new File(templateName))
    }

    def configure(Closure configureClosure) {
        configureClosure.delegate = job
        configureClosure.call()
    }

    def methodMissing(String name, args) {
        if (nodeAlreadyPresent(name)) {
            job.get(name)[0].value = args[0]
        } else {
            job.appendNode(name, args[0])
        }
    }

    static def createJob(String name, Closure configureClosure) {
        JenkinsJob job = new JenkinsJob()
        configureClosure.delegate = job
        configureClosure.call()
        new XmlNodePrinter(new PrintWriter(new FileWriter(new File('job.xml')))).print(job.job)
    }

    private boolean nodeAlreadyPresent(String nodeName) {
        return job.get(nodeName).size() > 0
    }

}