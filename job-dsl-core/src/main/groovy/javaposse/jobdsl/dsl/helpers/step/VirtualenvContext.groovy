package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class VirtualenvContext extends PythonContext implements Context {
    String name
    boolean clear = false
    boolean systemSitePackages = false

    void clear(boolean clear = true) {
        this.clear = clear
    }

    void name(String name) {
        this.name = name
    }

    void systemSitePackages(boolean systemSitePackages = true) {
        this.systemSitePackages = systemSitePackages
    }
}
