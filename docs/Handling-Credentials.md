There are three options for handling credentials and other secrets in Job DSL scripts.

* The first option involves the [Credentials Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Plugin) which
stores credentials in a secure manner in Jenkins global configuration and allows Job DSL scripts to reference credentials by their identifier. It is
also the most secure option because the credentials are never stored anywhere else and never even seen by the Job DSL script at all. But it only works for plugins that support the Credentials Plugin.
* The second option is to pass credentials to Job DSL script through build variables where they can be used to generate the job configs. This option is not as secure as using the Credentials Plugin, as passwords will be stored as clear text in the job configs (although not in the dsl scripts themselves), but seems to be the best solution when Credentials Plugin is not available.
* The third option is to use hard-coded credentials in Job DSL script. This option should be avoided at all cost because the
credentials will be stored in plain text in your dsl scripts and in the job configs.

The following sections show how to handle credentials in detail.

## The Credentials Plugin (and its relatives)

Using the [Credentials Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Plugin), the [Credentials Binding Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Binding+Plugin) or the [EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin) in the generated job is the most secure option, as the Job DSL Script never sees any of the credentials at all.

### The Credentials Plugin
The [Credentials Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Plugin) adds a new menu entry called
"Credentials" to the top-level Jenkins navigation, next to "Manage Jenkins". The Credentials page allows to manage
credentials for any plugins that can consume these credentials., e.g. the [Git
Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin) or the [Subversion
Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Subversion+Plugin).

Newer versions of the Credentials Plugin allow to specify a custom ID which should be used to assign a human readable
ID instead of using the auto-generated UUID.

The Job DSL allows to specify the credentials' ID as a reference when when configuring those plugins.
 
    // use the github-ci-key credentials for authentication with GitHub
    job('example-1') {
        scm {
            git {
                remote {
                    github('account/repo', 'ssh')
                    credentials('github-ci-key')
                }
            }
        }
    }
    
    // assign the jarsign-keystore credentials to the PASSWORD build variable
    job('example-2') {
        wrappers {
            credentialsBinding {
                usernamePassword('PASSWORD', 'jarsign-keystore')
            }
        }
    }

    // credentials can also be reference from a configure block
    job('example-3') {
        configure { project ->
            project / builders << 'org.foo.FooBuilder' {
                credentialsId('foo-password')
            }
        }
    }


### Passing Credentials as Build Variables in the generated job
As an alternative, the [Credentials Binding Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Binding+Plugin) or the [EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin) can be used to
pass credentials through build variables to the generated job. These can be used in shell scripts or other plugins that can consume build
variables but do not directly support the Credentials Plugin.


    job('example-4') {
        wrappers {
            injectPasswords()
        }
        steps {
            shell 'echo $MY_SECRET_TOKEN'
        }
    }


(A drawback of using global passwords with [EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin) is that **all** the existing credentials are injected into the job and not only the ones needed to
run the job. The [Credentials Binding Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Binding+Plugin) allows more fine-grained access to credentials.)

## Build Variables passed to the seed job

Similar to above, but credentials are passed to the seed job instead of the generated job. This option has the disadvantage, that credentials are exposed to the Job DSL scripts, and might be **stored in plaintext** in the job configurations.

All build variables are exposed to the Job DSL scripts as variables, see [User Power
Moves](User-Power-Moves#access-the-jenkins-environment-variables). There are several ways to define credentials as
build variables, e.g. the [EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin) provides a
"Inject passwords to the build as environment variables" setting to inject passwords either defined globally in 
"Configure System" or directly on a job.

    // use the FLOWDOCK_TOKEN variable to configure the Flowdock publisher
    job('example-5') {
        publishers {
            flowdock(FLOWDOCK_TOKEN) {
                unstable()
                success()
                aborted()
                failure()
                fixed()
                notBuilt()
            }
        }
    }
    
    // variables can also be using in configure blocks
    job('example-5') {
        configure { project ->
            project / builders << 'org.foo.FooBuilder' {
                userName(FOO_USER)
                password(FOO_PASSWORD)
            }
        }
    }

The following example shows a seed job configuration that loads DSL scripts from SCM and uses global passwords through
like build variables as in the examples above.

    job('seed') {
        scm {
            github('foo/ci-configuration')
        }
        wrappers {
            injectPasswords {
                injectGlobalPasswords()
            }
        }
        steps {
            dsl {
                external('jobs/*.groovy')
            }
        }
    }

Alternatively you can use [Credentials Binding Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Binding+Plugin).

## Hardcoding credentials in dsl scripts

Don't do that, please....
