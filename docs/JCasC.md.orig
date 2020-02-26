Support for Configuration as Code Plugin
========================================

See [Configuration as Code](https://plugins.jenkins.io/configuration-as-code) for an introduction to managing global
Jenkins settings as code.

The Job DSL plugin provides an extension to run Job DSL scripts when configuring Jenkins using Configuration as Code.
These scripts can be used to create an initial seed job.

To get started, add a root element called `jobs`. The given script will be executed by the Job DSL plugin.

```yml
jobs:
  - script: >
      multibranchPipelineJob('configuration-as-code') {
          branchSources {
              git {
                  id = 'configuration-as-code'
                  remote('https://github.com/jenkinsci/configuration-as-code-plugin.git')
              }
          }
      }
```

You can also fetch Job DSL scripts from a file or URL.

```yml
jobs:
  - file: ./jobdsl/job.groovy
```

```yml
jobs:
  - url: https://example.acme.org/job-dsl/testjob.groovy
```

You can reference multiple scripts, files, and URLs.

```yml
jobs:
  - script: >
    job('testJob1') {
        scm {
            git('git://github.com/quidryan/aws-sdk-test.git')
        }
        triggers {
            scm('H/15 * * * *')
        }
        steps {
            maven('-e clean test')
        }
    }

  - script: >
    job('testJob2') {
        scm {
            git('git://github.com/quidryan/aws-sdk-test.git')
        }
        triggers {
            scm('H/15 * * * *')
        }
        steps {
            maven('-e clean test')
        }
    }

  - file: ./jobdsl/job1.groovy
  - file: ./jobdsl/job2.groovy
```

You can pass values from the YAML file to the Job DSL script.

```yml
jobs:
  - providedEnv:
      SUPERHERO: 'Midnighter'
  - file: ./jobdsl/job.groovy
```

```groovy
//job.groovy
job('awesome-job') {
    description("favorite job of ${SUPERHERO}")
}
```
