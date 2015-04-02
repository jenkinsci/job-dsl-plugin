This is the in-depth documentation of the methods available on inside the _folder_ part of the DSL.

The [CloudBees Folders Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin) must be installed
to use folders.

```groovy
folder(String name) { // since 1.30
    name(String name) // deprecated since 1.30

    // DSL specific methods
    configure(Closure configureBlock)

    // common options
    displayName(String displayName)
    authorization(Closure closure) // since 1.31
}

folder(Closure folderClosure) // since 1.23, deprecated since 1.30
```

## Folder Options

### Name

```groovy
folder(String name) { // since 1.30
}
folder {              // since 1.23
    name(String name) // deprecated since 1.30
}
```

The name of the folder. Folders can be created within other folders by using the full path as name.

The name is treated as absolute to the Jenkins root by default, but the seed job can be configured to interpret names
relative to the seed job. (since 1.24)

```groovy
folder('example-1') {
}

folder('example-1/example-2') {
}
```

### Configure

```groovy
folder {
    configure(Closure configureBlock)
}

When an option is not supported by the DSL, [[The Configure Block]] can be used for extending the DSL.

```groovy
folder('example') {
    configure { folder ->
        folder / icon(class: 'org.example.MyFolderIcon')
    }
}
```

### Display Name

```groovy
folder {
    displayName(String displayName)
}
```

The name to display instead of the actual folder name.

```groovy
folder('project-a') {
    displayName('Project A')
}
```

### Authorization

```groovy
folder {
    authorization {
        permission(String)
        permission(String permission, String user)
        permissionAll(String user)
    }
}
```

Creates permission records.

The first form adds a specific permission, e.g. `'hudson.model.Item.Create:authenticated'`, as seen in config.xml.
The second form breaks apart the permission from the user name, to make scripting easier. The third form will add all
available permission for the user.

```groovy
// gives permission for the special authenticated group to create jobs in the folder
folder('example-1') {
    authorization {
        permission('hudson.model.Item.Create:authenticated')
    }
}

// gives discover permission for the special anonymous user
folder('example-2') {
    authorization {
        permission('hudson.model.Item.Discover', 'anonymous')
    }
}

// gives all permissions to the special anonymous user
folder('example-3') {
    authorization {
        permissionAll('anonymous')
    }
}
```

(since 1.31)
