import java.nio.charset.Charset

// read the file release.groovy from the seed job's workspace
// and configure a Groovy build step using that script
def releaseScript = readFileFromWorkspace('release.groovy')
job('example-1') {
    steps {
        groovyCommand(releaseScript)
    }
}

// read the file run.bat from a workspace of job project-a
// and use it to configure another job
def runScript = readFileFromWorkspace('project-a', 'run.bat')
job('example-2') {
    steps {
        batchFile(runScript)
    }
}

// read the file build.bat (encoded in Windows-31J) from the seed job's workspace
// and configure a Groovy build step using that script
def winScript = readFileFromWorkspace('build.bat', Charset.forName('windows-31j'))
job('example-3') {
    steps {
        batchFile(winScript)
    }
}
