Newer versions (14.1 or later) of [IntelliJ IDEA](https://www.jetbrains.com/idea/) can be configured to be aware of the
Job DSL beyond the basic Groovy syntax. That enables autocompletion and better syntax highlighting and will provide more
information about the DSL methods like "Parameter Info" and "Quick Documentation".

IDEA has a feature called [GroovyDSL](https://confluence.jetbrains.com/display/GRVY/Scripting+IDE+for+DSL+awareness)
which enables IDE support for custom Groovy DSLs. To configure support for the Job DSL, a GroovyDSL script with the
following content must be available on the classpath along with the `job-dsl-core` library.

    // enable DSL support in IDEA, see https://confluence.jetbrains.com/display/GRVY/Scripting+IDE+for+DSL+awareness
    
    def jobPath = /.*\/jobs\/.*\.groovy/
    
    def ctx = context(pathRegexp: jobPath)
    contributor(ctx, {
        delegatesTo(findClass('javaposse.jobdsl.dsl.DslFactory'))
    })

This GroovyDSL script will enable Job DSL IDE support in all script files matched by the `jobPath` regular expression,
in this case all file below a folder class `jobs` with file extension `.groovy`.

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
        jcenter()
        maven {
            url 'https://repo.jenkins-ci.org/public/'
        }
    }

    dependencies {
        compile 'org.jenkins-ci.plugins:job-dsl-core:@version@'
    }

In this example, the GroovyDSL script from above must be stored in `src/main/resources/idea.gdsl`.

For a complete example, have a look at the [Job DSL Gradle Example](https://github.com/sheehan/job-dsl-gradle-example).

IDE Support is currently not available for the [[Automatically Generated DSL]] or [[The Configure Block]].
