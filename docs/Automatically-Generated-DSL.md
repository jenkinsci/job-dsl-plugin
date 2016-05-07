Not all of the 1000+ Jenkins plugins are supported by the built-in DSL. If the
[API Viewer](https://jenkinsci.github.io/job-dsl-plugin/) does not list support for a certain plugin, the
automatically generated DSL can be used to fill the gap.

Use the embedded API Viewer to explore the available DSL methods. If your Jenkins instance is running at
http://localhost:8080, then the API viewer can be opened at http://localhost:8080/plugin/job-dsl/api-viewer. You can
find a link to embedded API Viewer in the "Process Job DSLs" build step and in the task bar of the seed job. Generated
methods are marked with a purple "Generated" tag. The generated DSL is *not* available in the online
[API Viewer](https://jenkinsci.github.io/job-dsl-plugin/).

If a methods is marked as required in the API viewer, it must be specified within it's context.

The following example shows a DSL script for using the generated DSL to configure the
[CVS Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CVS+Plugin).

    job('example') {
        scm {
            cvsscm {
                repositories {
                    cvsRepository {
                        cvsRoot(':pserver:username@hostname:/opt/path/to/a/repo')
                        passwordRequired(false)
                        password(null)
                        compressionLevel(-1)
                        repositoryBrowser {}
                        repositoryItems {
                            cvsRepositoryItem {
                                modules {
                                    cvsModule {
                                        localName('bar')
                                        projectsetFileName('bar')
                                        remoteName('foo')
                                    }
                                }
                                location {
                                    tagRepositoryLocation {
                                        tagName('test')
                                        useHeadIfNotFound(false)
                                    }
                                }
                            }
                        }
                    }
                }
                canUseUpdate(true)
                pruneEmptyDirectories(true)
                legacy(false)
                skipChangeLog(false)
                disableCvsQuiet(false)
                cleanOnFailedUpdate(false)
                forceCleanCopy(false)
            }
        }
    }

Be aware that [[IDE Support]] is currently not available for the generated DSL.

The generated DSL is only supported when running in Jenkins, e.g. it is not available when running from
the [command line](User-Power-Moves#run-a-dsl-script-locally) or in the [Playground](http://job-dsl.herokuapp.com/).
Use [[The Configure Block]] to generate custom config elements when not running in Jenkins.

The generated DSL will not work for all plugins, e.g. if a plugin does not use the `@DataBoundConstructor`
and `@DataBoundSetter` annotations to declare parameters. In that case [[The Configure Block]] can be used to generate
the config XML.
