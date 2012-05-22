Jenkins Job DSL / Plugin
========================

The Jenkins "Job DSL / Plugin" is made up of two parts: The Domain Specific Language (DSL) itself (which allows users to
describe Jobs using in a groovy-based language); and a Jenkins Plugin which manages the scripts and the updating of the
Jenkins jobs which are created and maintained as a result.

Background
----------
Jenkins is a wonderful system for managing builds, and people love using its UI to configure jobs.  Unfortunately, as
the number of jobs grows, maintaining them becomes tedious, and the paradigm of using a UI falls apart. Additionally,
since the common pattern in this situation is to copy jobs to create new ones, these "children" have a habit of
diverging from their original template and consequently it becomes difficult to maintain consistency between these jobs.

The Jenkins job-dsl-plugin attempts to solve this problem by allowing jobs to be defined with the absolute minimum
amount of definition, with the help of templates.

The goal is for your project to be able to define all the jobs they want to be related to their project, and declare
only the parts unique to their project, leaving the common stuff up to a template (or set of templates) that were
defined earlier, and which all jobs are based on.

For example, a project might want a unit test job, a nightly sonar build, a integration test job, and a promotion job:

```groovy
job {
    using 'TMPL-test'
    name 'PROJ-unit-tests'
    configure { node ->
        configureScm(node)
        triggers.'hudson.triggers.SCMTrigger'.spec = '*/15 * * * *'
        goals = '-e clean test'
    }
}

job {
    using 'TMPL-test'
    name 'PROJ-sonar'
    configure { node ->
        configureScm(node)
        triggers {
            'hudson.triggers.TimerTrigger'.spec = '15 13 * * *'
        }
        goals = 'sonar:sonar'
    }
}

job {
    using 'TMPL-test'
    name 'PROJ-integ-tests'
    configure { node ->
        configureScm(node)
        triggers.'hudson.triggers.TimerTrigger'.spec = '15 1,13 * * *'
        goals = '-e clean integTest'
    }
}

job {
    using 'TMPL-library-promotion'
    name 'PROJ-release'
    configure { node ->
        configureScm(node)
        goals = 'release'
    }
}
```

Manually creating these jobs wouldn't be too hard, but doing the same thing all over again for every new branch, or for
a hundred other projects is where it gets interesting (and by "interesting" we mean "difficult".) It's even likely that
to minimize the amount of configuration, someone might make a single parameterized job to do all these things in one
job, but then the history of the job is skewed and they were limited in some settings like triggers.

NOTE: The above example does depend on existing jobs which are used at templates, but in the future we hope to support
the complete definition of a jobs from scratch, sourced from a DSL file in source control. Another future feature is to
synchronize the jobs with the template, so if the template changes it all generated jobs would be updated with the
changes.

Building
--------
To build:

    ./gradlew build

To run Jenkins and test JPI:

    ./gradlew server

Build job-dsl.hpi to be installed in Jenkins:

    ./gradlew jpi

Usage
-----
1. Create your set of Jenkins jobs which wil server as the template jobs for all your DSL-Script-managed jobs. (It is a
good idea to use a naming convention for these jobs which clearly indicates that these are templates)
2. Create a further Jenkins Job using the Free-style project style which will manage your DSL Scripts.
("Job-DSL-Manager" is a good name for this)
3. Edit the job configuration of this free-style script-management job by adding a "Build Step" of "Process Job DSLs"
and enter your job script locations (relative to this job's "workspace") as a newline-separated list
4. Run the Job-DSL-Manager job to generate your new jobs from your scripts. (The build should run successfully and the
"build result" page should list the jobs which have been successfully created)
5. Finally, it is good practice to organise your Jenkins UI with some new tabs so that the management and template
jobs are not the first thing a user sees when they login

Authors
------
Justin Ryan <jryan@netflix.com>

Andrew Harmel-Law <andrew@harmel-law.com>

Artifacts
---------
(TBD) The library is built using Jenkins-on-Jenkins and is released via its update center.

Architecture
------------
The DSL job scripts are executed in the context of the Plugin-managed JobParent class which adds a "job" function to
define individual jobs.  This function creates a Job instance that has DSL-executing methods to enable the job
configuration via the DSL script. Both the JobParent and Job defer to an instance of a JobManagement class to load and
save the updated Jenkins config.xml.

The DslScriptLoader sets up and executes DSL scripts within the provided JobManagement instance. The FileJobManagement
class is an implementation of this which loads/saves Job configs from the local filesystem. While the Jenkins plugin is
running from ExecuteDslScripts, it creates a JobManagement instance which calls back into Jenkins.

The primary entry point of the plugin is ExecuteDslScripts, which is responsible for consuming the "seed" job's
definition of where the DSL files are. As it finds DSL script files, it sets up a JobManagement instance and calls the
aforementioned DslScriptLoader.

License
-------
Licensed under the Apache License, Version 2.0 (the “License”); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
“AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.