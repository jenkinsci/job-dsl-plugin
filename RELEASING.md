These are the steps to release the Maven-based Job DSL plugin.

* Ensure you have the latest code from origin: `git pull origin`
* Make sure tests still run: `mvn clean verify`
* Run locally to perform sanity check: `mvn hpi:run`
* Set `compatibleSinceVersion` to the new version if deprecated features have been removed
* Update the release notes, set the release date: `* 1.14 (Mar 31 2013)`
* Prepare and perform the release: `mvn release:prepare release:perform`
* File a pull request to update the release notes, adding the next version: `* 1.15 (unreleased)`
* Close all resolved issues in [JIRA](https://issues.jenkins-ci.org/secure/Dashboard.jspa?selectPageId=15341)
* Open a pull request to update the [Job DSL Playground](https://github.com/sheehan/job-dsl-playground) 
* Open a pull request to update the [Job DSL Gradle Example](https://github.com/sheehan/job-dsl-gradle-example)
* Open a pull request to update the [Job DSL Sample](https://github.com/unguiculus/job-dsl-sample)
* Wait up to twelve hours for it show up in the Update Center
* Follow the @jenkins_release twitter account and retweet the release!
