These are the steps to release the Maven-based Job DSL plugin.

* Ensure you have the latest code from origin: `git pull origin`
* Run locally to perform sanity check: `mvn hpi:run`
* Set `compatibleSinceVersion` to the new version if deprecated features have been removed
* Prepare and perform the release: `mvn release:prepare release:perform`
* Edit the [draft release notes](https://github.com/jenkinsci/job-dsl-plugin/releases) and publish them
* Run the [Push to the GitHub Wiki](https://github.com/jenkinsci/job-dsl-plugin/actions/workflows/wiki.yml) workflow to update the wiki with the new version
* Open a pull request to add the newly-released version to the API viewer in `job-dsl-plugin/pom.xml` and `job-dsl-plugin/src/main/hbs/root.hbs`
* Close all resolved issues in [JIRA](https://issues.jenkins-ci.org/secure/Dashboard.jspa?selectPageId=15341)
* Open a pull request to update the [Job DSL Playground](https://github.com/sheehan/job-dsl-playground)
* Open a pull request to update the [Job DSL Gradle Example](https://github.com/sheehan/job-dsl-gradle-example)
* Open a pull request to update the [Job DSL Sample](https://github.com/unguiculus/job-dsl-sample)
* Wait up to twelve hours for it show up in the Update Center
* Follow the @jenkins_release twitter account and retweet the release!
