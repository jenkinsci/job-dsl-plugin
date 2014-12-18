Targeted supported features:
```groovy
job {
    name = "${JOBNAME}-tests"
    configure { project ->
        // Simple block which adds if it doesn't already exist, (otherwise replaces(?) - we should document this)
        publishers.'hudson.tasks.JavadocArchiver' {
            // Simple body used in brackets
            javadocDir { 'build/javadoc' }

            // Simple assignment of text used via equals
            title = 'Java Documentation'

            // Support native boolean and other native types
            keepAll = true
        }

        // Updating an existing node, e.g. 'properties'
        properties {
            // Further navigation of existing sub-nodes
            'hudson.security.AuthorizationMatrixProperty' {
                // Add a new permission to existing Authorization Matrix
                // Suggestion - could we replace the '++' with 'add'/'append' and provide '++' as an alias?
                permission { 'hudson.model.Item.Configure:jryan' } ++

                // Replace all existing permissions since we assume everything is an update
                permission { hudson.model.Run.Delete:jryan }
            }
        }

        // Remove property from project
        // Suggestion - what about 'canRoam remove' with an alias of '--'?)
        canRoam--

        // Suggestion - could we change '++' to 'append'? I can see me missing / mis-interpreting the former.  We could always alias in the shorthand...
        builders {
            'hudson.tasks.Ant' {
                targets { 'sanitize findbugs build' }
                antName { 'Ant 1.8' }
                buildFile = 'build.xml'
            } ++ // This appends the hudson.tasks.Ant node to builders
        }

        // Use a custom function to append a trigger (which works within the current context)
        // Suggestion: could we clean this up a little?  The 'triggers(class:'vector')' piece is a repeat or is this just an error in this page?...
        triggers(class:'vector') + generateTrigger('*/10 * * * *')

        // Use another custom function to configure Git which has an XML node passed in to it
        configureGit(project, 'git://github.com/jenkinsci/analysis-collector-plugin.git', 'origin/master')
   }
}

// Convenience Method Examples:
// Function to add trigger, generate a node inside of current context, which in this case is triggers(class:'vector')
def generateTrigger(String cron) {
    'hudson.triggers.SCMTrigger' {
        spec { cron }
     }
}

// Function to configure git, takes Node and work with it
def configureGit(project, url, branch) {
  project.with {
    scm(class:'hudson.plugins.git.GitSCM') {
        userRemoteConfigs {
            'hudson.plugins.git.UserRemoteConfig' {
                // Using bracket to indicate text of node
                name { 'origin' }

                // Using equals to set node text
                refspec = '+refs/heads/*:refs/remotes/origin/*'

                // Variable to method, making sure delegates work
                url { url }
            }
        }
        branches {
           'hudson.plugins.git.BranchSpec' {
               name { branch }
           }
        }

        recursiveSubmodules { false }
     }
   }
}
```