It is possible to test Job DSL scripts outside of Jenkins, e.g. as part of a test-driven development round-trip in an
IDE. The [Jenkins Test Harness](https://github.com/jenkinsci/jenkins-test-harness) can be used to emulate a Jenkins
instance for tests.

To test DSL scripts in unit tests, it necessary to setup a build script for compiling and running the tests. The
following example uses [Gradle](http://gradle.org/) as build tool, but any suitable build tool can be used.

The content of the following code block must be saved in a file called `build.gradle`. This file should be stored at the
root of the same source code repository as the Job DSL scripts.

The build script assumes that the Job DSL scripts are located in a sub-directory called `jobs`. The `jobDslVersion` and
`jenkinsVersion` settings should match the versions running in the target installation. The dependencies listed as
`testPlugins` are Jenkins plugins that will be installed in the emulated Jenkins instance so that they are available
when running the DSL scripts, e.g. for testing [[extensions|Extending the DSL]] or [[Automatically Generated DSL]].

    apply plugin: 'groovy'

    ext {
        jobDslVersion = '@version@'
        jenkinsVersion = '@jenkinsVersion@'
    }

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

    configurations {
        testPlugins {}
    }

    dependencies {
        testCompile 'org.spockframework:spock-core:0.7-groovy-1.8'

        // Jenkins test harness dependencies
        testCompile 'org.jenkins-ci.main:jenkins-test-harness:2.8'
        testCompile "org.jenkins-ci.main:jenkins-war:${jenkinsVersion}"
        testCompile "org.jenkins-ci.main:jenkins-war:${jenkinsVersion}:war-for-test@jar"

        // Job DSL plugin including plugin dependencies
        testCompile "org.jenkins-ci.plugins:job-dsl:${jobDslVersion}"
        testCompile "org.jenkins-ci.plugins:job-dsl:${jobDslVersion}@jar"
        testCompile 'org.jenkins-ci.plugins:structs:1.6@jar'

        // plugins to install in test instance
        testPlugins 'org.jenkins-ci.plugins:ghprb:1.31.4'
        testPlugins 'com.coravy.hudson.plugins.github:github:1.19.0'
    }

    task resolveTestPlugins(type: Copy) {
        from configurations.testPlugins
        into new File(sourceSets.test.output.resourcesDir, 'test-dependencies')
        include '*.hpi'
        include '*.jpi'

        doLast {
            def baseNames = source.collect { it.name[0..it.name.lastIndexOf('.')-1] }
            new File(destinationDir, 'index').setText(baseNames.join('\n'), 'UTF-8')
        }
    }

    test {
        dependsOn tasks.resolveTestPlugins
        inputs.files sourceSets.jobs.groovy.srcDirs

        // set build directory for Jenkins test harness, JENKINS-26331
        systemProperty 'buildDirectory', project.buildDir.absolutePath
    }

For running unit test, a suitable unit test framework should be used. The following example uses
[Spock](http://docs.spockframework.org/).

The content of the following code block must be saved in a file called `src/test/groovy/JobScriptsSpec.groovy`.

The test specification will locate all Job DSL scripts within the `jobs` directory and create a test for each file.
The test will then run each script file to check the script for problems.

    import javaposse.jobdsl.dsl.DslScriptLoader
    import javaposse.jobdsl.plugin.JenkinsJobManagement
    import org.junit.ClassRule
    import org.jvnet.hudson.test.JenkinsRule
    import spock.lang.Shared
    import spock.lang.Specification
    import spock.lang.Unroll

    class JobScriptsSpec extends Specification {
        @Shared
        @ClassRule
        JenkinsRule jenkinsRule = new JenkinsRule()

        @Unroll
        def 'test script #file.name'(File file) {
            given:
            def jobManagement = new JenkinsJobManagement(System.out, [:], new File('.'))

            when:
            new DslScriptLoader(jobManagement).runScript(file.text)

            then:
            noExceptionThrown()

            where:
            file << jobFiles
        }

        static List<File> getJobFiles() {
            List<File> files = []
            new File('jobs').eachFileRecurse {
                if (it.name.endsWith('.groovy')) {
                    files << it
                }
            }
            files
        }
    }

To run the tests, execute `gradle test` from the command line in the directory containing the `build.gradle` file. It
will generate a test report in `build/reports/test/index.html`.

Have a look at the [Jenkins Job DSL Gradle example](https://github.com/sheehan/job-dsl-gradle-example) for a complex
example.

The Gradle build script can be combined with the one shown in [[IDE Support]] to enable IDE features like syntax
highlighting for Job DSL scripts.
