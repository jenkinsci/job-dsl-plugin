The `view` method behaves like the `job` method and will return a _View_ object.

Currently only a `type` attribute with value of `ListView`, `BuildPipelineView`, `SectionedView`, `NestedView`,
`DeliveryPipelineView` or `BuildMonitorView` is supported. When no type is specified, a list view will be generated.

```groovy
view(type: ListView) {
  name 'project-view'
}
```