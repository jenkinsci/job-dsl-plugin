package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper

/**
 * This class adds support for the Publishers from
 * <a href="https://wiki.jenkins-ci.org/display/JENKINS/Static+Code+Analysis+Plug-ins">Static Code Analysis Plugins</a>
 *
 * The class {@link javaposse.jobdsl.dsl.helpers.publisher.PublisherContext} uses this class
 * as a delegate to make the corresponding methods appear as methods of the <code>publishers</code> Closure.
 *
 * Every Publisher has the following common set of xml, which is not added to the corresponding xml struckture in the javadoc
 * of the method:
 * <pre>
 * {@code
 * <healthy></healthy>
 * <unHealthy></unHealthy>
 * <thresholdLimit>low</thresholdLimit>
 * <pluginName><<Name of the plugin>> </pluginName>
 * <defaultEncoding></defaultEncoding>
 * <canRunOnFailed>false</canRunOnFailed>
 * <useStableBuildAsReference>false</useStableBuildAsReference>
 * <useDeltaValues>false</useDeltaValues>
 * <thresholds>
 *   <unstableTotalAll>1</unstableTotalAll>
 *   <unstableTotalHigh>2</unstableTotalHigh>
 *   <unstableTotalNormal>3</unstableTotalNormal>
 *   <unstableTotalLow>4</unstableTotalLow>
 *   <unstableNewAll>9</unstableNewAll>
 *   <unstableNewHigh>10</unstableNewHigh>
 *   <unstableNewNormal>11</unstableNewNormal>
 *   <unstableNewLow>12</unstableNewLow>
 *   <failedTotalAll>5</failedTotalAll>
 *   <failedTotalHigh>6</failedTotalHigh>
 *   <failedTotalNormal>7</failedTotalNormal>
 *   <failedTotalLow>8</failedTotalLow>
 *   <failedNewAll>13</failedNewAll>
 *   <failedNewHigh>14</failedNewHigh>
 *   <failedNewNormal>15</failedNewNormal>
 *   <failedNewLow>16</failedNewLow>
 * </thresholds>
 * <shouldDetectModules>false</shouldDetectModules>
 * <dontComputeNew>false</dontComputeNew>
 * <doNotResolveRelativePaths>true</doNotResolveRelativePaths>
 * }
 * </pre>
 */
class StaticAnalysisPublisherContext {
    List<Node> publisherNodes
    JobManagement jobManagement

    StaticAnalysisPublisherContext(List<Node> publisherNodes, JobManagement jobManagement) {
        this.publisherNodes = publisherNodes
        this.jobManagement = jobManagement
    }

    /**
     * Configures the findbugs publisher
     *
     * <pre>
     * {@code
     * <hudson.plugins.findbugs.FindBugsPublisher>
     *   ...
     *   <pattern>**&#47;findbugsXml.xml</pattern>
     *   <isRankActivated>false</isRankActivated>
     * </hudson.plugins.findbugs.FindBugsPublisher>
     * }
     * </pre>
     **/
    def findbugs(String pattern, boolean isRankActivated = false, Closure staticAnalysisClosure = null) {
        StaticAnalysisContext staticAnalysisContext = new StaticAnalysisContext()
        AbstractContextHelper.executeInContext(staticAnalysisClosure, staticAnalysisContext)

        publisherNodes << NodeBuilder.newInstance().'hudson.plugins.findbugs.FindBugsPublisher' {
            addStaticAnalysisContextAndPattern(delegate, staticAnalysisContext, pattern)
            delegate.isRankActivated(isRankActivated)
        }
    }

    /**
     * Configures the PMD Publisher
     *
     * <pre>
     * {@code
     * <hudson.plugins.pmd.PmdPublisher>
     *   ...
     *   <pattern>pmd.xml</pattern>
     * </hudson.plugins.pmd.PmdPublisher>
     * }
     * </pre>
     */
    def pmd(String pattern, Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
                'hudson.plugins.pmd.PmdPublisher',
                staticAnalysisClosure,
                pattern
        )
    }

    /**
     * Configures the Checkstyle Publisher
     *
     * <pre>
     * {@code
     * <hudson.plugins.checkstyle.CheckStylePublisher>
     *   ...
     *   <pattern>checkstyle.xml</pattern>
     * </hudson.plugins.checkstyle.CheckStylePublisher>
     * }
     * </pre>
     */
    def checkstyle(String pattern, Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
                'hudson.plugins.checkstyle.CheckStylePublisher',
                staticAnalysisClosure,
                pattern
        )
    }

    /**
     * Configures the JsHint checkstyle Publisher
     *
     * <pre>
     * {@code
     * <hudson.plugins.jshint.CheckStylePublisher>
     *   ...
     *   <pattern>checkstyle.xml</pattern>
     * </hudson.plugins.jshint.CheckStylePublisher>
     * }
     * </pre>
     */
    def jshint(String pattern, Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
            'hudson.plugins.jshint.CheckStylePublisher',
             staticAnalysisClosure,
             pattern
        )
    }

    /**
     * Configures the DRY Publisher
     *
     * <pre>
     * {@code
     * <hudson.plugins.dry.DryPublisher>
     *   ...
     *   <pattern>cpd.xml</pattern>
     *   <highThreshold>85</highThreshold>
     *   <normalThreshold>13</normalThreshold>
     * </hudson.plugins.dry.DryPublisher>
     * }
     * </pre>
     */
    def dry(String pattern, highThreshold = 50, normalThreshold = 25, Closure staticAnalysisClosure = null) {
        StaticAnalysisContext staticAnalysisContext = new StaticAnalysisContext()
        AbstractContextHelper.executeInContext(staticAnalysisClosure, staticAnalysisContext)

        publisherNodes << NodeBuilder.newInstance().'hudson.plugins.dry.DryPublisher' {
            addStaticAnalysisContextAndPattern(delegate, staticAnalysisContext, pattern)
            delegate.highThreshold(highThreshold)
            delegate.normalThreshold(normalThreshold)
        }
    }

    /**
     * Configures the Task Scanner Publisher
     *
     * <pre>
     * {@code
     * <hudson.plugins.tasks.TasksPublisher>
     *   ...
     *   <pattern>*.java</pattern>
     *   <high>FIXM</high>
     *   <normal>TOD</normal>
     *   <low>LOW</low>
     *   <ignoreCase>true</ignoreCase>
     *   <excludePattern>*.groovy</excludePattern>
     * </hudson.plugins.tasks.TasksPublisher>
     * }
     * </pre>
     */
    def tasks(String pattern, excludePattern = '', high = '', normal = '', low = '', ignoreCase = false, Closure staticAnalysisClosure = null) {
        StaticAnalysisContext staticAnalysisContext = new StaticAnalysisContext()
        AbstractContextHelper.executeInContext(staticAnalysisClosure, staticAnalysisContext)

        publisherNodes << NodeBuilder.newInstance().'hudson.plugins.tasks.TasksPublisher' {
            addStaticAnalysisContextAndPattern(delegate, staticAnalysisContext, pattern)
            delegate.high(high)
            delegate.normal(normal)
            delegate.low(low)
            delegate.ignoreCase(ignoreCase)
            delegate.excludePattern(excludePattern)
        }
    }

    /**
     * Configures the CCM Publisher
     *
     * <pre>
     * {@code
     * <hudson.plugins.ccm.CcmPublisher>
     *   ...
     *   <pattern>ccm.xml</pattern>
     * </hudson.plugins.ccm.CcmPublisher>
     * }
     * </pre>
     */
    def ccm(String pattern, Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
                'hudson.plugins.ccm.CcmPublisher',
                staticAnalysisClosure,
                pattern
        )
    }

    /**
     * Configures the Android Lint Publisher
     *
     * <pre>
     * {@code
     * <org.jenkinsci.plugins.android__lint.LintPublisher>
     *   ...
     *   <pattern>lint.xml</pattern>
     * </org.jenkinsci.plugins.android__lint.LintPublisher>
     * }
     * </pre>
     */
    def androidLint(String pattern, Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
                'org.jenkinsci.plugins.android__lint.LintPublisher',
                staticAnalysisClosure,
                pattern
        )
    }

    /**
     * Configures the OWASP Dependency-Check Publisher
     *
     * <pre>
     * {@code
     * <org.jenkinsci.plugins.DependencyCheck.DependencyCheckPublisher>
     *   ...
     *   <pattern>dep.xml</pattern>
     * </org.jenkinsci.plugins.DependencyCheck.DependencyCheckPublisher>
     * }
     * </pre>
     */
    def dependencyCheck(String pattern, Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
                'org.jenkinsci.plugins.DependencyCheck.DependencyCheckPublisher',
                staticAnalysisClosure,
                pattern
        )
    }

    /**
     * Configures the Compiler Warnings Publisher
     *
     * <pre>
     * {@code
     * <hudson.plugins.warnings.WarningsPublisher>
     *   ...
     *   <includePattern>.*include.*</includePattern>
     *   <excludePattern>.*exclude.*</excludePattern>
     *   <consoleParsers>
     *     <hudson.plugins.warnings.ConsoleParser>
     *       <parserName>Java Compiler (javac)</parserName>
     *     </hudson.plugins.warnings.ConsoleParser>
     *   </consoleParsers>
     *   <parserConfigurations>
     *     <hudson.plugins.warnings.ParserConfiguration>
     *       <pattern>*.log</pattern>
     *       <parserName>Java Compiler (javac)</parserName>
     *     </hudson.plugins.warnings.ParserConfiguration>
     *   </parserConfigurations>
     * </hudson.plugins.warnings.WarningsPublisher>
     * }
     * </pre>
     */
    def warnings(List consoleParsers, Map parserConfigurations = [:], Closure warningsClosure = null) {
        jobManagement.requireMinimumPluginVersion('warnings', '4.0')
        WarningsContext warningsContext = new WarningsContext()
        AbstractContextHelper.executeInContext(warningsClosure,  warningsContext)

        def nodeBuilder = NodeBuilder.newInstance()
        publisherNodes << nodeBuilder.'hudson.plugins.warnings.WarningsPublisher' {
            addStaticAnalysisContext(delegate,  warningsContext)
            includePattern(warningsContext.includePattern)
            excludePattern(warningsContext.excludePattern)
            nodeBuilder.consoleParsers {
                (consoleParsers ?: []).each { name ->
                    nodeBuilder.'hudson.plugins.warnings.ConsoleParser' {
                        parserName(name)
                    }
                }
            }
            nodeBuilder.parserConfigurations {
                (parserConfigurations ?: [:]).each { name, filePattern ->
                    nodeBuilder.'hudson.plugins.warnings.ParserConfiguration' {
                        pattern(filePattern)
                        parserName(name)
                    }
                }
            }
        }
    }

    private createDefaultStaticAnalysisNode(String publisherClassName, Closure staticAnalysisClosure, String pattern) {
        StaticAnalysisContext staticAnalysisContext = new StaticAnalysisContext()
        AbstractContextHelper.executeInContext(staticAnalysisClosure, staticAnalysisContext)

        NodeBuilder.newInstance()."${publisherClassName}" {
            addStaticAnalysisContextAndPattern(delegate, staticAnalysisContext, pattern)
        }
    }

    private def addStaticAnalysisContext(nodeBuilder, StaticAnalysisContext context) {
        nodeBuilder.with {
            healthy(context.healthy)
            unHealthy(context.unHealthy)
            thresholdLimit(context.thresholdLimit)
            defaultEncoding(context.defaultEncoding)
            canRunOnFailed(context.canRunOnFailed)
            useStableBuildAsReference(context.useStableBuildAsReference)
            useDeltaValues(context.useDeltaValues)
            thresholds {
                context.thresholdMap.each { threshold, values ->
                    values.each { value, num ->
                        nodeBuilder."${threshold}${value.capitalize()}"(num)
                    }
                }
            }
            shouldDetectModules(context.shouldDetectModules)
            dontComputeNew(context.dontComputeNew)
            doNotResolveRelativePaths(context.doNotResolveRelativePaths)
        }
    }

    private def addStaticAnalysisPattern(nodeBuilder, String pattern) {
        nodeBuilder.pattern(pattern)
    }

    private def addStaticAnalysisContextAndPattern(nodeBuilder, StaticAnalysisContext context, String pattern) {
        addStaticAnalysisContext(nodeBuilder, context)
        addStaticAnalysisPattern(nodeBuilder, pattern)
    }

}

