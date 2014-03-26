Not all of these have been asked a lot, but they're good things to know.

1. **Q: Is there a Java Binding for Job-DSL?** - _A:_ No, not at the moment. The DSL relies heavily of both closures for contexts  and methodMissing/propertyMissing for XML generation. There's not currently a good Java equivalent of either of those. It's not to say that it couldn't be done, it'd just be ugly.
1. **Q: Why won't my DSL script run? - using an element name already defined in an outer scope** - _A:_ a common problem that many folks see using is "name()" in a configure block because this clashes with the name method of the job element. In general, you cannot use any element name (in this case "name") which is already an identifier in an outer scope. In these cases, you need to fallback to low-level API. See more [on the forum](https://groups.google.com/forum/#!msg/job-dsl-plugin/ljdB2BMEEz8/AUIXcbreknIJ). 
1. **Q: Why isn't my generated config.xml like I was expecting, there was no error when the seed job ran?** - _A:_ Have you got the plugins installed in your Jenkins that the generated config.xml will refer to? If not, your the seed job may run, but you won't see any errors.  Take a look in the Jenkins log for things like `com.thoughworks.xstream.mapper.CannotResolveClassException: org.jenkinsci.plugins.multiplescms.MultiSCM ...`
1. **Q: When using `shell("php symfony test:unit --xml=log/build_$BUILD_NUMBER.xml")` I'm getting the error `FATAL: No such property: xml for class: java.lang.String`?** - _A:_ You should use ${BUILD_NUMBER}. Or if you need the variable to be set like that in the job, you need to escape the $ (e.g \$BUILD_NUMBER).
1. **Q: What's a canonical example of a good Pull request?** - _A:_ https://github.com/jenkinsci/job-dsl-plugin/pull/91
1. **Q: In my unit tests, how can I use the Jenkins test harness to simulate running my plugin on slaves?** - _A:_ https://github.com/jenkinsci/job-node-stalker-plugin/blob/master/src/test/java/com/datalex/jenkins/plugins/nodestalker/wrapper/MyNodeAssignementActionFunctionalTest.java
1. **Q: How do I add a Step in a particular Order using the configure block?** - _A:_ 
```
steps {
    shell(bumpVersionStep)
}				
// Configure the XCode builder plugin - outside of the job DSL
configure { project ->	
    project/builders << 'au.com.rayh.XCodeBuilder'{
        // Blah blah
    }
}
steps {
    shell(buildLoggerStep)
    shell(saveEnvironmentStep)
}
```