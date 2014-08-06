**WARNING 1:** Using mixin classes causes memory-leaks. See
[JENKINS-23762](https://issues.jenkins-ci.org/browse/JENKINS-23762) for details.

**WARNING 2:** Using monkey-patching causes race conditions when concurrent seed jobs use different monkey patches for
the same class.

If you want to introduce custom DSL commands, you can monkey-patch **Context classes from your scripts. For example:
```groovy
import javaposse.jobdsl.dsl.helpers.*

ScmContext.metaClass.our_p4 = { String mapping ->
    p4(mapping,'our_username','our_password') { node ->
        node / p4Port('p4ip:p4port')
        node / p4Client("\${JOB_NAME}")
        node / createWorkspace('true')
        node / updateView('true')
    }
}

// Then you can use the new DSL command(s) just as any other:
job {  
    // ....
    scm {
        our_p4("//p4path/... //workspace/...")
    }
}
```
If you find these monkey-patches useful, consider [submitting them to the Core DSL as an enhancement.](Contributing-to-the-job-dsl-plugin-Project).

## Using helper files

You can put the code introducing new DSL commands into a separate file (called common.groovy in the example below) and run the monkey-patching code when the file is imported (using a static initializer).
```groovy
import javaposse.jobdsl.dsl.helpers.*
class common {
    static {
        ScmContext.metaClass.our_p4 = { String mapping ->
            p4(mapping,'our_username','our_password') { node ->
                node / p4Port('p4ip:p4port')
                node / p4Client("\${JOB_NAME}")
                node / createWorkspace('true')
                node / updateView('true')
            }
        }
    }
}
```
Then just `import common` and `use(common) {` in your script and the DSL command(s) are available.
```groovy
import common

use(common) {
    job {
         // ....
         scm {
             our_p4("....")
         }
    }
}
```
Have a look at source job-dsl-core/src/main/groovy/javaposse/jobdsl/dsl/helpers/* to see which Context classes you can monkey-patch to introduce new DSL commands.
