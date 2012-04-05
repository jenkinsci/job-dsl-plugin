Jenkins Job DSL
===============

The "Jenkins Job DSL" is made up of two parts: The Domain Specific Language (DSL) itself (which allows users to describe Jobs using in a groovy-based language); and a Jenkins Plugin which manages the scripts and the updating of the Jenkins jobs which are created and maintained as a result.

Background
------
Jenkins is a wonderful system for managing builds, and people love using its UI to configure jobs.  As the number of jobs grows maintaining them becomes tedious, and the paradigm of using a UI falls apart. And since the common pattern is to copy jobs to create new ones, they have a habit of diverging from their original template while it also becomes difficult to maintain consistency between these jobs. The job-dsl-plugin attempts to solve this problem by allowing jobs to be defined with the absolute minimum definition, with the help of templates.

The goal is for a project to define all the jobs they want to be related to their project, declaring only the parts unique to their project. For example, a project might want a unit test job, a nightly sonar build, a integration test job, and a promotion job:

'''groovy
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
    name 'PROJ-integ-tests'
    configure { node ->
        configureScm(node)
        triggers.'hudson.triggers.TimerTrigger'.spec = '15 1,13 * * *'
        goals = '-e clean integTest'
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
    using 'TMPL-library-promotion'
    name 'PROJ-release'
    configure { node ->
        configureScm(node)
        goals = 'release'
    }
}
'''

Manually creating these jobs wouldn't be too hard, but doing the same thing for a branch or a hundred other projects is where it gets interesting and difficult. It's even likely that to minimize the amount of configuration, someone might make a single parameterized job to do all these things in one job, but then the history of the job is skewed and they were limited in some settings like triggers. This example does depend on existing jobs which are used at templates, but in the future we hope to support the complete definition of a jobs from scratch, sourced from a DSL file in source control. Another future feature is to synchronize the jobs with the template, so if the template changes it all generated jobs would be updated with the changes.

Building
--------
./gradlew build # To build
./gradlew server # To run Jenkins and test JPI
./gradlew jpi # Build job-dsl.hpi to be installed in Jenkins

Author
------
Justin Ryan <jryan@netflix.com>
Andrew Harmel-Law <andrew@harmel-law.com>

Artifacts
---------
(TBD) The library is built using Jenkins-on-Jenkins and is released via its update center.

Architecture
------------
The DSL job scripts are executed in the context of the Plugin-managed JobParent class which adds a "job" function to define individual jobs.  This function creates a Job instance that has DSL executing methods to support the job being configured. Both the JobParent and Job defer to an instance of a JobManagement class to load and save an updated Jenkins config.xml.

The DslScriptLoader will setup and execute DSL scripts within the provided JobManagement instance. The FileJobManagement class is an implementation of this which loads/saves Job configs from the local filesystem. While the Jenkins plugin is running from ExecuteDslScripts, it creates a JobManagement instance which calls back into Jenkins. The primary entry point of the plugin is ExecuteDslScripts, which is responsible for consuming the "seed" job's definition of where the DSL files are. As it finds DSL script files, it sets up a JobManagement instance and calls DslScriptLoader.

License
-------
Licensed under the Apache License, Version 2.0 (the “License”); you may not use this file except in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
