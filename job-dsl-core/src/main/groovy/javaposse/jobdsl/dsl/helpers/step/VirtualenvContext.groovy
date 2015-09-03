package javaposse.jobdsl.dsl.helpers.step

class VirtualenvContext extends PythonContext {
    String name
    boolean clear = false
    boolean systemSitePackages = false

    /**
     * If set, the virtualenv environment will be deleted and re-created on each build. Defaults to {@code false}.
     */
    void clear(boolean clear = true) {
        this.clear = clear
    }

    /**
     * Sets the name for the virtualenv.
     */
    void name(String name) {
        this.name = name
    }

    /**
     * Provides access to the global site-packages directory for the virtual environment. Defaults to {@code false}.
     */
    void systemSitePackages(boolean systemSitePackages = true) {
        this.systemSitePackages = systemSitePackages
    }
}
