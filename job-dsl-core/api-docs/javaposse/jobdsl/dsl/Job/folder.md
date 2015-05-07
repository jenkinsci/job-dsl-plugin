The `folder` method behaves like the `job` method and will return a _Folder_ object.

Folders will be created before jobs and views to ensure that a folder exists before entries are created.

```groovy
folder {
  name 'project-a'
  displayName 'Project A'
}
```

Items can be created within folders by using the full path as job name.

```groovy
folder {
  name 'project-a'
}

job {
  name 'project-a/compile'
}

view {
  name 'project-a/pipeline'
}

folder {
  name 'project-a/testing'
}
```