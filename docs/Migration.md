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
