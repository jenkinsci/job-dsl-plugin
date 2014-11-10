## Migrating to 1.28

### DSL Method Return Values

Prior to version 1.28 most DSL methods had an undocumented return value. Since 1.28 DSL methods do not return a value
except for the methods defined in `javaposse.jobdsl.dsl.DslFactory`.

### Git Clone Options

The `reference` and `shallowClone` methods in the `git` closure are deprecated, use the cloneOptions(boolean shallowClone = false, String referencePath, Integer timeoutMinutes) method instead.

DSL prior to 1.28
```groovy
job {
    scm {
        git {
            shallowClone(true)
            reference('/foo/bar')
        }
    }
}
```

DSL since 1.28
```groovy
job {
    scm {
        git {
            cloneOptions(true, '/foo/bar', 20)
        }
    }
}
```

## Migrating to 1.27

### Job Name

The `name` method variant with a closure parameter in the `job` closure is deprecated, use the string argument variant
instead.

DSL prior to 1.27
```groovy
job {
    name {
        'foo'
    }
}
```

DSL since 1.27
```groovy
job {
    name('foo')
}

### Permissions

In version 1.27 undocumented `permission` methods in the `job` context have been deprecated. Use the `authorization`
context instead.

DSL prior to 1.27
```groovy
job {
    permission('hudson.model.Item.Configure:jill')
    permission(Permissions.ItemRead, 'jack')
    permission('RunUpdate', 'joe')
}
```

DSL since 1.27
```groovy
job {
    authorization {
        permission('hudson.model.Item.Configure:jill')
        permission(Permissions.ItemRead, 'jack')
        permission('RunUpdate', 'joe')
    }
}
```

## Migrating to 1.26

### Archive JUnit Report

In version 1.26 the archiveJunit method with boolean arguments has been deprecated and has been replaced by a closure
variant.

DSL prior to 1.26
```groovy
job {
    publishers {
        archiveJunit('**/target/surefire-reports/*.xml', true, true, true)
    }
}
```

DSL since 1.26
```groovy
job {
    publishers {
        archiveJunit('**/target/surefire-reports/*.xml') {
            retainLongStdout()
            testDataPublishers {
                allowClaimingOfFailedTests()
                publishTestAttachments()
            }
        }
    }
}
```

See the [[Job Reference]] for further details.

### Xvnc

In version 1.26 the xvnc method with one boolean argument has been deprecated and has been replaced by a closure
variant.

DSL prior to 1.26
```groovy
job {
    wrappers {
        xvnc(true)
    }
}
```

DSL since 1.26
```groovy
job {
    wrappers {
        xvnc {
            takeScreenshot()
        }
    }
}
```

See the [[Job Reference]] for further details.

### Gerrit Trigger

The usage "short names" in the event closure is deprecated and has been replaced by explicit DSL methods for each event.

DSL prior to 1.26
```groovy
job {
    triggers {
        gerrit {
            events {
                ChangeAbandoned
                ChangeMerged
                ChangeRestored
                CommentAdded
                DraftPublished
                PatchsetCreated
                RefUpdated
            }
        }
    }
}
```

DSL since 1.26
```groovy
job {
    triggers {
        gerrit {
            events {
                changeAbandoned()
                changeMerged()
                changeRestored()
                commentAdded()
                draftPublished()
                patchsetCreated()
                refUpdated()
            }
        }
    }
}
```

See the [[Job Reference]] for further details.

### AbstractStepContext

`javaposse.jobdsl.dsl.helpers.step.AbstractStepContext` has been removed, use
`javaposse.jobdsl.dsl.helpers.step.StepContext` instead.

DSL prior to 1.26
```groovy
AbstractStepContext.metaClass.myStep = { ... }
}
```

DSL since 1.26
```groovy
StepContext.metaClass.myStep = { ... }
```

## Migrating to 1.24

### Build Timeout

In version 1.24 the dsl for the build timeout plugin has been modified and the
generated xml requires a newer version of the build timeout plugin.
The old dsl still works but has been deprecated.

DSL prior to 1.24
```groovy
timeout(String type) { //type is one of: 'absolute', 'elastic', 'likelyStuck'
    limit 15       // timeout in minutes
    percentage 200 // percentage of runtime to consider a build timed out
}

timeout(35, false)
```

DSL since 1.24
```groovy
timeout {
   absolute(15)
   failBuild()
   writeDescription('Build failed due to timeout after {0} minutes')
}

timeout {
    absolute(35)
    failBuild(false)
}
```

See the [[Job Reference]] for further details.

### Gerrit Trigger

Before 1.24, the Gerrit trigger configuration used hardwired configuration for unset label configurations
(successfulVerified +1, failedVerified -1, everything else 0, these are the default values of the central Gerrit trigger
plugin configuration). Now the Gerrit trigger configuration correctly honors central configuration of labels. If you use
non-default labels in your central configuration, you might need to change the trigger label configuration of your jobs.

See the [[Job Reference]] for further details.

## Migrating to 1.20

In version 1.20, some implementation classes have been moved to work around a [bug](http://jira.codehaus.org/browse/GROOVY-5875) in Groovy. When these classes have been used to [extend the DSL](Extending-the-DSL-from-your-Job-Scripts), import statements and fully qualified class names have to be adjusted.

## Migrating to 1.19

In version 1.19 all build wrapper elements have been moved from the job element to a wrappers sub-element. When upgrading to 1.19 or later, the wrapper elements have to moved as shown below.

DSL prior to 1.19:

```groovy
job {
    ...
    runOnSameNodeAs 'other', true
    rvm 'ruby-1.9.2-p290'
    timeout 60
    allocatePorts('PORT_A', 'PORT_B')
    sshAgent 'deloy-key'
    ...
}
```

DSL since 1.19:

```groovy
job {
    ...
    wrappers {
        runOnSameNodeAs 'other', true
        rvm 'ruby-1.9.2-p290'
        timeout 60
        allocatePorts('PORT_A', 'PORT_B')
        sshAgent 'deloy-key'
    }
    ...
}
```
