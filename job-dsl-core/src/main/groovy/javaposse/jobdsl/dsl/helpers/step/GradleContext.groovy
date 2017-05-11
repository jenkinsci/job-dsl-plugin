package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class GradleContext extends AbstractContext {
    final List<String> tasks = []
    final List<String> switches = []
    boolean useWrapper = true
    String description = ''
    String rootBuildScriptDir = ''
    String buildFile = ''
    boolean fromRootBuildScriptDir = true
    boolean makeExecutable
    boolean useWorkspaceAsHome
    boolean passAsProperties
    String gradleName = '(Default)'
    Closure configureBlock

    protected GradleContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Specifies the Gradle tasks to be invoked. Can be called multiple times to add more tasks.
     */
    void tasks(String tasks) {
        this.tasks << tasks
    }

    /**
     * Specifies the Gradle switches to be invoked. Can be called multiple times to add more switches.
     */
    void switches(String switches) {
        this.switches << switches
    }

    /**
     * Use the Gradle wrapper to invoke the build script. Defaults to {@code true}.
     */
    void useWrapper(boolean useWrapper = true) {
        this.useWrapper = useWrapper
    }

    /**
     * Sets a description for the build step.
     */
    @Deprecated
    void description(String description) {
        this.description = description
    }

    /**
     * Specifies the directory containing the {@code build.gradle}. Defaults to the workspace directory.
     */
    void rootBuildScriptDir(String rootBuildScriptDir) {
        this.rootBuildScriptDir = rootBuildScriptDir
    }

    /**
     * Specifies the name of the Gradle build script file. Defaults to {@code build.gradle}.
     */
    void buildFile(String buildFile) {
        this.buildFile = buildFile
    }

    /**
     * Defaults to {@code true}.
     */
    void fromRootBuildScriptDir(boolean fromRootBuildScriptDir = true) {
        this.fromRootBuildScriptDir = fromRootBuildScriptDir
    }

    /**
     * Specifies the name of the Gradle installation to use if not using the wrapper.
     */
    void gradleName(String gradleName) {
        this.gradleName = gradleName
    }

    /**
     * Sets the executable flag on the wrapper script file before invoking the file. Defaults to {@code false}.
     */
    void makeExecutable(boolean makeExecutable = true) {
        this.makeExecutable = makeExecutable
    }

    /**
     * Passes job parameters as Gradle properties. Defaults to {@code false}.
     *
     * @since 1.49
     */
    @RequiresPlugin(id = 'gradle', minimumVersion = '1.25')
    void passAsProperties(boolean passAsProperties = true) {
        this.passAsProperties = passAsProperties
    }

    /**
     * If set, uses the workspace as {@code GRADLE_USER_HOME}. Defaults to {@code false}.
     *
     * @since 1.40
     */
    void useWorkspaceAsHome(boolean useWorkspaceAsHome = true) {
        this.useWorkspaceAsHome  = useWorkspaceAsHome
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code hudson.plugins.gradle.Gradle} node is passed into the
     * configure block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }
}
