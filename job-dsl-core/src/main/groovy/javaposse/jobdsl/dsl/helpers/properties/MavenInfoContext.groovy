package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.Context

class MavenInfoContext implements Context {
    boolean assignName = false
    boolean assignDescription = false
    String name
    String description
    String modulePattern
    String interestingDependenciesPattern

    /**
     * Sets the name from main module pom.xml (from tag <name>).
     */
    void assignName(String name) {
        this.assignName = true
        this.name = name
    }

    /**
     * Sets the description from main module pom.xml (from tag <description>).
     */
    void assignDescription(String description) {
        this.assignDescription = true
        this.description = description
    }

    /**
     * Sets the regex pattern to find module to extract versions, name, description.
     */
    void modulePattern(String modulePattern) {
        this.modulePattern = modulePattern
    }

    /**
     * Sets the pattern to find interesting dependencies.
     */
    void interestingDependenciesPattern(String interestingDependenciesPattern) {
        this.interestingDependenciesPattern = interestingDependenciesPattern
    }
}
