Via [configuration-as-code-plugin](https://plugins.jenkins.io/configuration-as-code) also known as JCasC

It is possible to configure initial seed jobs through a yaml config file.  
The basics for job dsl is you have a root element called `jobs` that will be parsed to configure via job dsl

Examples of config file

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

You can also fetch your job dsl from a file or URL

```yml
jobs:
  - file: ./jobdsl/job.groovy
```

```yml
jobs:
  - url: https://raw.githubusercontent.com/jenkinsci/job-dsl-plugin/master/job-dsl-plugin/src/test/resources/javaposse/jobdsl/plugin/testjob.groovy
```

You can reference multiple scripts, files, and urls

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

You can pass values from the yaml file to the job dsl script

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
