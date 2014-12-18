Groovy has a built-in feature to load external libraries into the current runtime, it's called Grab. The job-dsl supports this syntax. It can be used to easily pull down a dependency from Maven Central or download a corporate binary of dsl conventions. Here's some example syntax:

```groovy
@GrabResolver('http://artifacts/jenkins')
@Grab('com.company.build:dsl-conventions:1.2')

use(company.Convention) { // Using Groovy's Category
    job {
       name "Standard job for repo 3"
       myCompanyScm("repo3") // Method comes from Convention.myCompanyScm
    }
}
```

