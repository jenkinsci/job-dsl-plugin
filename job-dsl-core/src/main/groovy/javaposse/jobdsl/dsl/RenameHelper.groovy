package javaposse.jobdsl.dsl

abstract class RenameHelper {
    void renameJobMatching(String fromRegex, String destination) throws IOException {
        Set<String> matchingJobs = allJobNames().findAll { it ==~ fromRegex }
        if (matchingJobs.size() > 1) {
            throw new IllegalArgumentException("Multiple jobs to rename found: ${matchingJobs}")
        }
        if (matchingJobs.empty) {
            return
        }
        String sourceName = matchingJobs.iterator().next()

        if (sourceName != destination) {
            renameJob(sourceName, destination)
        }
    }

    abstract Set<String> allJobNames()

    abstract void renameJob(String source, String destination) throws IOException
}
