These are the steps to release a gradle-based Jenkins plugin, assuming the release plugin isn't being used.

* Ensure you have the latest code from origin: _git pull origin_
* Make sure tests still run: _./gradlew test_
* Run locally to perform santity check: _./gradlew :job-dsl-plugin:server_
* Edit gradle.properties to strip -SNAPSHOT from version: _vi gradle.properties_
* Ensure everything is checked in: _git commit -am "Releasing 1.14"_
* Ensure you have your Jenkins credentials in ~/.jenkins-ci.org: _cat ~/.jenkins-ci.org_
```
userName=yourUsername
password=IHeartJenkins
```
* Deploy: _./gradlew uploadArchives_
* Update and commit the release notes.
* Publish the docs: _./gradlew publishDocs_
* Tag the source as it is: _git tag -a job-dsl-1.14 -m "Staging 1.14"_
* Increment the version in gradle.properties and append "-SNAPSHOT": _echo "version=1.15-SNAPSHOT">gradle.properties_
* Commit the updated version number: _git commit -am "Bumping to next rev"_
* Push the two new commit and the tag back to GitHub: _git push --tags origin_
* Wait up to twelve hours for it show up in the Update Center
* Follow the @jenkins_release twitter account and retweet the release!