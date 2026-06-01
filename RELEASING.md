These are the steps to release the Maven-based Job DSL plugin.

* Merge a pull request with one of the labels that generate a new release. Refer to the [release drafter labels](https://github.com/jenkinsci/.github/blob/master/.github/release-drafter.yml) for details
* Wait for the automated release process to complete
* Review the [release notes](https://github.com/jenkinsci/job-dsl-plugin/releases) and update them if necessary
* Run the [Push to the GitHub Wiki](https://github.com/jenkinsci/job-dsl-plugin/actions/workflows/wiki.yml) workflow to update the wiki with the new version
* Open a pull request to add the newly-released version to the API viewer in `job-dsl-plugin/pom.xml` and `job-dsl-plugin/src/main/hbs/root.hbs`
* Close all resolved issues in [GitHub](https://github.com/jenkinsci/job-dsl-plugin/issues)
