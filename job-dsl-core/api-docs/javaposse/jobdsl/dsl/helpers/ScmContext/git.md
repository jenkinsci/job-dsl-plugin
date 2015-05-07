Adds a Git SCM source. The first variant can be used for advanced configuration (since 1.20), the other two variants are
shortcuts for simpler Git SCM configurations.

The GitHub variants will derive the Git URL from the ownerAndProject, protocol and host parameters. Valid protocols are
`https`, `ssh` and `git`. They also configure the source browser to point to GitHub.

The Git plugin has a lot of configurable options, which are currently not all supported by the DSL. A  configure block
can be used to add more options.

Version 2.0 or later of the Git Plugin is required to use `cloneTimeout` or Jenkins managed credentials for Git
authentication. The arguments for the credentials method is the description field or the UUID generated from
Jenkins | Manage Jenkins | Manage Credentials. The easiest way to find this value, is to navigate
Jenkins | Credentials | Global credentials | (Key Name). Then look at the description in parenthesis or
using the UUID in the URL.

When Git Plugin version 2.0 or later is used, `mergeOptions` can be called multiple times to merge more than one branch.

Examples:

```groovy
// checkout repo1 to a sub directory and clean the workspace after checkout
git {
    remote {
        name('remoteB')
        url('git@server:account/repo1.git')
    }
    clean()
    relativeTargetDir('repo1')
}
```

```groovy
// add the upstream repo as second remote and merge branch featureA with master
git {
    remote {
        name('origin')
        url('git@serverA:account/repo1.git')
    }
    remote {
        name('upstream')
        url('git@serverB:account/repo1.git')
    }
    branch('featureA')
    mergeOptions('upstream', 'master')
}
```

```groovy
// add user name and email options
git('git@git') { node -> // is hudson.plugins.git.GitSCM
    node / gitConfigName('DSL User')
    node / gitConfigEmail('me@me.com')
}
```

```groovy
// add a Git SCM for the GitHub repository job-dsl-plugin of GitHub user jenkinsci
github('jenkinsci/job-dsl-plugin')
```

```groovy
// add a Git SCM for a GitHub repository and use the given credentials for authentication
git {
    remote {
        github('account/repo', 'ssh')
        credentials('GitHub CI Key')
    }
}
```