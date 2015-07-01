Newer versions (14.1 or later) of [IntelliJ IDEA](https://www.jetbrains.com/idea/) can be configured to be aware of the
Job DSL beyond the basic Groovy syntax. That enables better syntax highlighting and will provide more information about
the DSL methods like "Parameter Info" and "Quick Documentation".

An easy way to automatically configure IDEA is to use a [Gradle](https://gradle.org/) build file for the project setup.
The content of the following code block must be saved in a file called `build.gradle`. This file should be stored in the
same source code repository as the Job DSL scripts. It assumes that the Job DSL scripts are located in a sub-directory
called `jobs`. In IDEA, use the Gradle build file to open the project. That will setup the project along with all
necessary libraries to enable the DSL support. Note that you must provide a local installation of Gradle or use the
[Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) since IDEA does not contain a Gradle
installation.

    apply plugin: 'groovy'

    sourceSets {
        jobs {
            groovy {
                srcDirs 'jobs'
            }
        }
    }

    repositories {
        mavenCentral()
        maven {
            url 'http://repo.jenkins-ci.org/releases/'
        }
    }

    dependencies {
        compile 'org.jenkins-ci.plugins:job-dsl-core:1.35'
    }

Job DSL scripts must use the `.groovy` file name extension to let IDEA apply the DSL support.

For a complete example, have a look at the [Job DSL Gradle Example](https://github.com/sheehan/job-dsl-gradle-example).


Implementation Details
----------------------

The Job DSL core library provides a
[script](https://github.com/jenkinsci/job-dsl-plugin/blob/job-dsl-1.35/job-dsl-core/src/main/resources/javaposse/jobdsl/dsl/idea.gdsl)
for the
[IDEA GroovyDSL scripting framework](https://confluence.jetbrains.com/display/GRVY/Scripting+IDE+for+DSL+awareness).
IDEA will automatically pick-up the script and enable the DSL support when the Job DSL core library is used as an
external library in a project.