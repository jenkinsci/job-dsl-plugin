Why does a method defined in an outer scope takes precedence of a method defined in an inner scope?
---------------------------------------------------------------------------------------------------

The Groovy SDK loop methods (e.g. `each`) use the default closure resolve strategy
[OWNER_FIRST](http://docs.groovy-lang.org/latest/html/gapi/groovy/lang/Closure.html#OWNER_FIRST) and thus methods from
an outer scope take precedence over methods from inner scopes.

The problem can be solved by calling the
[delegate](http://docs.groovy-lang.org/latest/html/gapi/groovy/lang/Closure.html#getDelegate%28%29) of the closure
directly, as shown in this example:

    def pipelines = [
        [name: 'foo', startJob: 'foo_start'],
        [name: 'bar', startJob: 'bar_start'],
    ]

    nestedView('Pipelines') {
        views {
            pipelines.each { def pipeline ->
                // call delegate.buildPipelineView to create a nested view
                delegate.buildPipelineView("${pipeline.name} Pipeline") {
                    selectedJob(pipeline.startJob)
                }
            }
        }
    }

Avoiding `each` by using `for` loops will also fix the problem.


Why isn't my generated job like I was expecting, there was no error when the seed job ran?
------------------------------------------------------------------------------------------

Have you got the plugins installed in your Jenkins that the generated `config.xml` will refer to? If not, your the seed
job may run, but you won't see any errors.  Take a look in the Jenkins log for things like
`com.thoughworks.xstream.mapper.CannotResolveClassException: org.jenkinsci.plugins.multiplescms.MultiSCM ...`.

You may also need to restart Jenkins after installing plugins if the generated configuration is present in `config.xml`,
but not shown on the job's configuration page.


How do I add a step in a particular order using the configure block?
--------------------------------------------------------------------

    job('example') {
        steps {
            shell('echo "first step"')
        }
        // configure the XCode builder plugin as second step
        configure { project ->
            project / builders << 'au.com.rayh.XCodeBuilder' {
                // add necessary elements here
            }
        }
        steps {
            shell('echo "last step"')
        }
    }


Is there a Java binding for Job-DSL?
------------------------------------

No, not at the moment. The DSL relies heavily of both closures for contexts and `methodMissing` / `propertyMissing` for
XML generation. Currently there is no good Java equivalent of either of those. It's not to say that it couldn't be done,
it'd just be ugly.
