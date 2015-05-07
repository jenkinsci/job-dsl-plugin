With the first variant, you can read in a file from the current workspace anywhere in the script. This assumes that
you checked out some source control as part of the job processing the DSL. This can be useful when populating fields on
a generated job, e.g.

```groovy
job {
    steps {
        shell(readFileFromWorkspace('build.sh')
    }
}
```

And with the second variant, you can read a file from the workspace of any job. This can be used to set the description
of a job from a file in the job's workspace. The method will return `null` when the job or the file does not exist or
the job has no workspace, e.g. when it has not been built yet.

```groovy
job {
    name('acme-tests')
    description(readFileFromWorkspace('acme-tests', 'README.txt'))
}
```