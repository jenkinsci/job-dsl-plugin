The method will return a _Job_ object that can be re-used and passed around. E.g.

```groovy
def myJob = job {
    name 'SimpleJob'
}
myJob.with {
    description 'A Simple Job'
}
```

A job can have optional attributes. Currently only a `type` attribute with value of `Freeform`, `Maven`, `Multijob`,
`BuildFlow`, `MatrixJob` or `Workflow`is supported. When no type is specified, a free-style job will be generated. Some
methods will only be available in some job types, e.g. `phase` can only be used in Multijob. Each DSL method documents
where they are relevant.

```groovy
job(type: Maven) {
  name 'maven-job'
}
```