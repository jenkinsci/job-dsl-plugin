Jenkins Job DSL / Plugin
========================

The Jenkins "Job DSL / Plugin" is made up of two parts: The Domain Specific Language (DSL) that allows users to
describe Jobs using in a groovy-based language, and a Jenkins Plugin which manages the scripts and the updating of the
Jenkins jobs which are created and maintained as a result.

Background
----------
Jenkins is a wonderful system for managing builds, and people love using its UI to configure jobs.  Unfortunately, as
the number of jobs grows, maintaining them becomes tedious, and the paradigm of using a UI falls apart. Additionally,
the common pattern in this situation is to copy jobs to create new ones, these "children" have a habit of
diverging from their original "template" and consequently it becomes difficult to maintain consistency between these jobs.

The Jenkins job-dsl-plugin attempts to solve this problem by allowing jobs to be defined with the absolute minimum
necessary in a programmatic form, with the help of templates that are synced with the generated jobs.  The goal is for 
your project to be able to define all the jobs they want to be related to their project, delcaring their intent for the 
jobs, leaving the common stuff up to a template that were defined earlier or hidden behind the DSL.

For example, a project might want a unit test job, a nightly sonar build, a integration test job, and a promotion job.
In this stituation, we have a job named TMPL-test that has a majority of a setup already defined (Chuck Norris plugin, 
email plugin, etc). We'll reuse that job for the test-based jobs, using the "using" DSL command. The template doesn't
have our specific SCM settings, and we want to re-use the same one, so we'll define it in a variable called "gitUrl". 
We'll also limit whom can run the release job.

```groovy
def gitUrl = 'git://github.com/JavaPosseRoundup/job-dsl-plugin.git'

job {
    using 'TMPL-test'
    name 'PROJ-unit-tests'
    scm {
        git(gitUrl)
    }
    triggers {
        scm('*/15 * * * *')
    }
    steps { // build step
        maven('-e clean test')
    }
}

job {
    using 'TMPL-test'
    name 'PROJ-sonar'
    scm {
        git(gitUrl)
    }
    triggers {
        cron('15 13 * * *')
    }
    steps {
        maven('sonar:sonar')
    }
}

job {
    using 'TMPL-test'
    name 'PROJ-integ-tests'
    scm {
        git(gitUrl)
    }
    triggers {
        cron('15 1,13 * * *')
    }
    steps {
        maven('-e clean integTest')
    }
}

job {
    // No template, not needed
    name 'PROJ-release'
    scm {
        git(gitUrl)
    }
    // No Trigger
    authorization { 
        // Limit builds to just jack and jill
        permission(Permissions.ItemBuild, 'jill')
        permission(Permissions.ItemBuild, 'jack')
    }
    steps { 
        maven('release')
        shell('cleanup.sh')
    }
}
```

NOTE: This example does depend on existing jobs which are used at templates, and hence won't run "out of the box". Read 
the wiki for isolated examples and step-by-step guides to get this example working.

Manually creating these jobs wouldn't be too hard, but doing the same thing all over again for every new branch or for
a hundred other projects is where it gets interesting (and by "interesting" we mean "difficult"). An anti-pattern that 
people use in Jenkins to minimize the amount of job configuration, they make a single parameterized job to do all these 
things in one job, but then the history of the job is skewed and they were limited in some settings like triggers. This
provides a much more powerful way of defining them. Likewise, if using the jenkins plugin of the DSL then when the 
template is changed, the test jobs will be re-created! 

Please refer to the wiki, https://github.com/JavaPosseRoundup/job-dsl-plugin/wiki, for further documentation and examples.

Features
--------
* DSL
** Scriptable via Groovy
** Direct control of XML, so that anything possible in a config.xml is possible via the DSL
** Helper methods for common job configurations, e.g. scm, triggers, build steps
* Plugin
** DSL can be put directly in a job
** DSL can be put into SCM and polled using standard SCM triggering
** Multiple DSLs can be referenced as a time
** Tracks Templates used, will update derivative jobs when template is changed

Basic Usage
-----
See the wiki for specific steps and other examples.

1. Create your set of Jenkins jobs which will serve as the templates (It is a good idea to use a naming convention for
these jobs which clearly indicates that these are templates)
2. Create a Jenkins Job using the Free-style project style to run your DSL Scripts. This is called a "Seed" job.
3. Configure the seed job, by adding a "Build Step" of type "Process Job DSLs" and paste in the body of the DSL.
4. Run the seed to generate your new jobs from your script. When successful, and the "build result" page will
list the jobs which have been successfully created.
5. Finally, it is good practice to organise your Jenkins UI with some new tabs so that the management and template
jobs are not the first thing a user sees when they login

Building
--------
To build:

    ./gradlew build

To run Jenkins and test JPI:

    ./gradlew server

Build job-dsl.hpi to be installed in Jenkins:

    ./gradlew jpi

Authors
-------
Justin Ryan <jryan@netflix.com>

Andrew Harmel-Law <andrew@harmel-law.com>


Mailing List
------------
To track progress and ask questions head over the mailing list, https://groups.google.com/d/forum/job-dsl-plugin

Artifacts
---------
(TBD) The library is built using Jenkins-on-Jenkins and is released via its update center.

License
-------
Licensed under the Apache License, Version 2.0 (the “License”); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
“AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
