Not all of the 1000+ Jenkins plugins are supported by the built-in DSL. If the
[API Viewer](https://jenkinsci.github.io/job-dsl-plugin/) does not list support for a certain plugin, the
automatically generated DSL can be used to fill the gap.

The [Structs Plugin](https://github.com/jenkinsci/structs-plugin) must be installed to use the generated DSL.

The generated DSL syntax can be derived from the config XML. Jenkins will show the config XML when appending
`/config.xml` to a job's URL, e.g. `http://localhost:8080/job/example/config.xml`. The following example shows a config
XML snippet for the [Unity3dBuilder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Unity3dBuilder+Plugin) and the
[Log Parser Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Log+Parser+Plugin).

    <project>
      <builders>
        <org.jenkinsci.plugins.unity3d.Unity3dBuilder>
          <unity3dName/>
          <argLine>-batchmode</argLine>
          <unstableReturnCodes>2,3</unstableReturnCodes>
        </org.jenkinsci.plugins.unity3d.Unity3dBuilder>
      </builders>
      <publishers>
        <hudson.plugins.logparser.LogParserPublisher>
          <useProjectRule>true</useProjectRule>
          <projectRulePath>unitybuilder.parser</projectRulePath>
          <unstableOnWarning>true</unstableOnWarning>
          <failBuildOnError>true</failBuildOnError>
          <showGraphs>false</showGraphs>
        </hudson.plugins.logparser.LogParserPublisher>
      </publishers>
    </project>

Class names must be shortened by using the uncapitalized unqualified name, e.g. `unity3dBuilder` for
`org.jenkinsci.plugins.unity3d.Unity3dBuilder`. Property names can be taken directly from the XML, like
`unstableReturnCodes` or `failBuildOnError` in the example. Default values are used if a property is omitted, so not all
properties need to be specified.

Applying these rules leads to the following Job DSL script for the example above. Note that `builders` are called
`steps` in the DSL.

    job('example') {
      steps {
        unity3dBuilder {
          argLine('-batchmode')
          unstableReturnCodes('2,3')
        }
      }
      publishers {
        logParserPublisher {
           useProjectRule(true)
           projectRulePath('unitybuilder.parser')
           unstableOnWarning(true)
           failBuildOnError(true)
        }
      }
    }

The following example shows a CVS SCM configuration, which is more complex with deeper nested elements.

    <project>
      <scm class="hudson.scm.CVSSCM">
        <flatten>true</flatten>
        <repositories>
          <hudson.scm.CvsRepository>
            <cvsRoot>:pserver:username@hostname:/opt/path/to/a/repo</cvsRoot>
            <repositoryItems>
              <hudson.scm.CvsRepositoryItem>
                <modules>
                  <hudson.scm.CvsModule>
                    <localName>bar</localName>
                    <remoteName>foo</remoteName>
                  </hudson.scm.CvsModule>
                </modules>
                <location class="hudson.scm.CvsRepositoryLocation$TagRepositoryLocation">
                  <locationType>TAG</locationType>
                  <locationName>test</locationName>
                  <useHeadIfNotFound>false</useHeadIfNotFound>
                </location>
              </hudson.scm.CvsRepositoryItem>
            </repositoryItems>
            <compressionLevel>-1</compressionLevel>
            <excludedRegions>
              <hudson.scm.ExcludedRegion>
                <pattern/>
              </hudson.scm.ExcludedRegion>
            </excludedRegions>
            <passwordRequired>false</passwordRequired>
          </hudson.scm.CvsRepository>
        </repositories>
        <canUseUpdate>true</canUseUpdate>
        <skipChangeLog>false</skipChangeLog>
        <pruneEmptyDirectories>true</pruneEmptyDirectories>
        <disableCvsQuiet>false</disableCvsQuiet>
        <cleanOnFailedUpdate>false</cleanOnFailedUpdate>
        <forceCleanCopy>false</forceCleanCopy>
      </scm>
    </project>

In this example the class name for the `scm` element is specified by the `class` attribute. As mentioned above, it must
be shortened by using the uncapitalized class name. In this case the unqualified name is all upper case (`CVSSCM`), so
it must be specified all lower case (`cvsscm`). Property values can contain classes names which must be shortened as
well, e.g. `cvsRepositoryItem` for `hudson.scm.CvsRepositoryItem`. Some class names denote inner classes which are
recognizable by a dollar sign (`$`) in the name. Inner classes must be shortened to the unqualified name of the inner
class, which is the part after the dollar sign, e.g. `tagRepositoryLocation` for
`CvsRepositoryLocation$TagRepositoryLocation`.

The generated DSL uses runtime metadata extracted from plugin classes annotated by `@DataBoundConstructor` and
`@DataBoundSetter` annotations. In some cases the parameter and property names used with these annotations do not match
the serialized XML. In the CVS example, the `locationName` property of `TagRepositoryLocation` does not match the
parameter name of the `@DataBoundConstructor`. The DSL script will fail when using `locationName`, but a helpful message
is printed to the console output:

    Processing provided DSL script
    ERROR: (script, line 17) No signature of method: locationName() is applicable for argument types: (java.lang.String) values: [test]
    Possible solutions: tagName(), useHeadIfNotFound()

In this case `locationName` probably needs to be replaced by `tagName`. A look at the
[source code](https://github.com/jenkinsci/cvs-plugin/blob/cvs-2.12/src/main/java/hudson/scm/CvsRepositoryLocation.java#L135)
of the CVS plugin can confirm the assumption.

This is the final DSL script for a CVS SCM configuration: 

    job('example') {
      scm {
        cvsscm {
          repositories {
            cvsRepository {
              cvsRoot(':pserver:username@hostname:/opt/path/to/a/repo')
              repositoryItems {
                cvsRepositoryItem {
                  modules {
                    cvsModule {
                      localName('bar')
                      remoteName('foo')
                    }
                  }
                  location {
                    tagRepositoryLocation {
                      tagName('test')
                    }
                  }                            
                }
              }
            }
          }
          canUseUpdate(true)
          pruneEmptyDirectories(true)
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
