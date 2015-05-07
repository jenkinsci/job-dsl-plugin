The `configFile` method behaves like the `job` method and will return a _ConfigFile_ object.

A config file can have optional attributes. Currently only a `type` attribute with value of `Custom` or `MavenSettings`
is supported. When no type is specified, a custom config file will be generated.

Config files will be created before jobs to ensure that the file exists before it is referenced.

```groovy
configFile {
  name 'my-config'
  comment 'My important configuration'
  content '<some-xml/>'
}

configFile(type: MavenSettings) {
  name 'central-mirror'
  content readFileFromWorkspace('maven-settings/central-mirror.xml')
}
```