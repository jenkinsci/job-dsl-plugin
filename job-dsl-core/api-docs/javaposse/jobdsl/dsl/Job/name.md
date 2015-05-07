The Name of the job, **required**. This could be a static name but given the power of Groovy you could get very fancy with the these.

If using the [folders plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Free+Enterprise+Plugins#CloudBeesFreeEnterprisePlugins-FoldersPlugin), the full path to the job can be used. e.g.
```groovy
name('path/to/myjob')
```
Note that the folders must already exist. (Available since 1.17).

The name is treated as absolute to the Jenkins root by default, but the seed job can be configured to interpret names
relative to the seed job. (since 1.24)