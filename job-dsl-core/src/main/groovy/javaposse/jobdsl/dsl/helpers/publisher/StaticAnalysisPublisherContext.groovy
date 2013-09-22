package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper

class StaticAnalysisPublisherContext {
    List<Node> publisherNodes

    StaticAnalysisPublisherContext(List<Node> publisherNodes) {
        this.publisherNodes = publisherNodes
    }

    /**
     * Configures the findbugs publisher
     *
     * <pre>
     * {@code
     * <hudson.plugins.findbugs.FindBugsPublisher>
     *   <healthy/>
     *   <unHealthy/>
     *   <thresholdLimit>low</thresholdLimit>
     *   <defaultEncoding/>
     *   <canRunOnFailed>false</canRunOnFailed>
     *   <useStableBuildAsReference>false</useStableBuildAsReference>
     *   <useDeltaValues>false</useDeltaValues>
     *   <thresholds>
     *     <unstableTotalAll>1</unstableTotalAll>
     *     <unstableTotalHigh>2</unstableTotalHigh>
     *     <unstableTotalNormal>3</unstableTotalNormal>
     *     <unstableTotalLow>4</unstableTotalLow>
     *     <failedTotalAll>5</failedTotalAll>
     *     <failedTotalHigh>6</failedTotalHigh>
     *     <failedTotalNormal>7</failedTotalNormal>
     *     <failedTotalLow>8</failedTotalLow>
     *     <unstableNewAll>9</unstableNewAll>
     *     <unstableNewHigh>10</unstableNewHigh>
     *     <unstableNewNormal>11</unstableNewNormal>
     *     <unstableNewLow>12</unstableNewLow>
     *     <failedNewAll>13</failedNewAll>
     *     <failedNewHigh>14</failedNewHigh>
     *     <failedNewNormal>15</failedNewNormal>
     *     <failedNewLow>16</failedNewLow>
     *   </thresholds>
     *   <shouldDetectModules>false</shouldDetectModules>
     *   <dontComputeNew>false</dontComputeNew>
     *   <doNotResolveRelativePaths>true</doNotResolveRelativePaths>
     *   <pattern>**findbugsXml.xml</pattern>
     *   <isRankActivated>false</isRankActivated>
     * </hudson.plugins.findbugs.FindBugsPublisher>
     * }
     * </pre>
     **/
    def findbugs(String pattern, boolean isRankActivated = false, Closure findbugsClosure = null) {
        StaticAnalysisContext findbugsContext = new StaticAnalysisContext()
        AbstractContextHelper.executeInContext(findbugsClosure, findbugsContext)

        publisherNodes << NodeBuilder.newInstance().'hudson.plugins.findbugs.FindBugsPublisher' {
            addStaticAnalysisContextAndPattern(delegate, findbugsContext, pattern)
            delegate.isRankActivated(isRankActivated)
        }
    }

    /**
     * Configures the PMD Publisher
     *
     * <pre>
     * {@code
     * <hudson.plugins.pmd.PmdPublisher>
     *   <healthy>3</healthy>
     *   <unHealthy>20</unHealthy>
     *   <thresholdLimit>high</thresholdLimit>
     *   <defaultEncoding>UTF-8</defaultEncoding>
     *   <canRunOnFailed>true</canRunOnFailed>
     *   <useStableBuildAsReference>true</useStableBuildAsReference>
     *   <useDeltaValues>true</useDeltaValues>
     *   <thresholds>
     *     <unstableTotalAll>1</unstableTotalAll>
     *     <unstableTotalHigh>2</unstableTotalHigh>
     *     <unstableTotalNormal>3</unstableTotalNormal>
     *     <unstableTotalLow>4</unstableTotalLow>
     *     <failedTotalAll>5</failedTotalAll>
     *     <failedTotalHigh>6</failedTotalHigh>
     *     <failedTotalNormal>7</failedTotalNormal>
     *     <failedTotalLow>8</failedTotalLow>
     *     <unstableNewAll>9</unstableNewAll>
     *     <unstableNewHigh>10</unstableNewHigh>
     *     <unstableNewNormal>11</unstableNewNormal>
     *     <unstableNewLow>12</unstableNewLow>
     *     <failedNewAll>13</failedNewAll>
     *     <failedNewHigh>14</failedNewHigh>
     *     <failedNewNormal>15</failedNewNormal>
     *     <failedNewLow>16</failedNewLow>
     *   </thresholds>
     *   <shouldDetectModules>true</shouldDetectModules>
     *   <dontComputeNew>false</dontComputeNew>
     *   <doNotResolveRelativePaths>true</doNotResolveRelativePaths>
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
     *   <healthy>3</healthy>
     *   <unHealthy>20</unHealthy>
     *   <thresholdLimit>high</thresholdLimit>
     *   <defaultEncoding>UTF-8</defaultEncoding>
     *   <canRunOnFailed>true</canRunOnFailed>
     *   <useStableBuildAsReference>true</useStableBuildAsReference>
     *   <useDeltaValues>true</useDeltaValues>
     *   <thresholds>
     *     <unstableTotalAll>1</unstableTotalAll>
     *     <unstableTotalHigh>2</unstableTotalHigh>
     *     <unstableTotalNormal>3</unstableTotalNormal>
     *     <unstableTotalLow>4</unstableTotalLow>
     *     <failedTotalAll>5</failedTotalAll>
     *     <failedTotalHigh>6</failedTotalHigh>
     *     <failedTotalNormal>7</failedTotalNormal>
     *     <failedTotalLow>8</failedTotalLow>
     *     <unstableNewAll>9</unstableNewAll>
     *     <unstableNewHigh>10</unstableNewHigh>
     *     <unstableNewNormal>11</unstableNewNormal>
     *     <unstableNewLow>12</unstableNewLow>
     *     <failedNewAll>13</failedNewAll>
     *     <failedNewHigh>14</failedNewHigh>
     *     <failedNewNormal>15</failedNewNormal>
     *     <failedNewLow>16</failedNewLow>
     *   </thresholds>
     *   <shouldDetectModules>true</shouldDetectModules>
     *   <dontComputeNew>false</dontComputeNew>
     *   <doNotResolveRelativePaths>true</doNotResolveRelativePaths>
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
     * Configures the DRY Publisher
     *
     * <pre>
     * {@code
     * <hudson.plugins.dry.DryPublisher>
     *   <healthy>3</healthy>
     *   <unHealthy>20</unHealthy>
     *   <thresholdLimit>high</thresholdLimit>
     *   <defaultEncoding>UTF-8</defaultEncoding>
     *   <canRunOnFailed>true</canRunOnFailed>
     *   <useStableBuildAsReference>true</useStableBuildAsReference>
     *   <useDeltaValues>true</useDeltaValues>
     *   <thresholds>
     *     <unstableTotalAll>1</unstableTotalAll>
     *     <unstableTotalHigh>2</unstableTotalHigh>
     *     <unstableTotalNormal>3</unstableTotalNormal>
     *     <unstableTotalLow>4</unstableTotalLow>
     *     <failedTotalAll>5</failedTotalAll>
     *     <failedTotalHigh>6</failedTotalHigh>
     *     <failedTotalNormal>7</failedTotalNormal>
     *     <failedTotalLow>8</failedTotalLow>
     *     <unstableNewAll>9</unstableNewAll>
     *     <unstableNewHigh>10</unstableNewHigh>
     *     <unstableNewNormal>11</unstableNewNormal>
     *     <unstableNewLow>12</unstableNewLow>
     *     <failedNewAll>13</failedNewAll>
     *     <failedNewHigh>14</failedNewHigh>
     *     <failedNewNormal>15</failedNewNormal>
     *     <failedNewLow>16</failedNewLow>
     *   </thresholds>
     *   <shouldDetectModules>true</shouldDetectModules>
     *   <dontComputeNew>false</dontComputeNew>
     *   <doNotResolveRelativePaths>true</doNotResolveRelativePaths>
     *   <pattern>cpd.xml</pattern>
     *   <highThreshold>85</highThreshold>
     *   <normalThreshold>13</normalThreshold>
     * </hudson.plugins.dry.DryPublisher>
     * }
     * </pre>
     */
    def dry(String pattern, highThreshold = 50, normalThreshold = 25, Closure dryClosure = null) {
        StaticAnalysisContext staticAnalysisContext = new StaticAnalysisContext()
        AbstractContextHelper.executeInContext(dryClosure, staticAnalysisContext)

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
     *   <healthy>3</healthy>
     *   <unHealthy>20</unHealthy>
     *   <thresholdLimit>high</thresholdLimit>
     *   <defaultEncoding>UTF-8</defaultEncoding>
     *   <canRunOnFailed>true</canRunOnFailed>
     *   <useStableBuildAsReference>true</useStableBuildAsReference>
     *   <useDeltaValues>true</useDeltaValues>
     *   <thresholds>
     *     <unstableTotalAll>1</unstableTotalAll>
     *     <unstableTotalHigh>2</unstableTotalHigh>
     *     <unstableTotalNormal>3</unstableTotalNormal>
     *     <unstableTotalLow>4</unstableTotalLow>
     *     <failedTotalAll>5</failedTotalAll>
     *     <failedTotalHigh>6</failedTotalHigh>
     *     <failedTotalNormal>7</failedTotalNormal>
     *     <failedTotalLow>8</failedTotalLow>
     *     <unstableNewAll>9</unstableNewAll>
     *     <unstableNewHigh>10</unstableNewHigh>
     *     <unstableNewNormal>11</unstableNewNormal>
     *     <unstableNewLow>12</unstableNewLow>
     *     <failedNewAll>13</failedNewAll>
     *     <failedNewHigh>14</failedNewHigh>
     *     <failedNewNormal>15</failedNewNormal>
     *     <failedNewLow>16</failedNewLow>
     *   </thresholds>
     *   <shouldDetectModules>true</shouldDetectModules>
     *   <dontComputeNew>false</dontComputeNew>
     *   <doNotResolveRelativePaths>true</doNotResolveRelativePaths>
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
     *   <healthy>3</healthy>
     *   <unHealthy>20</unHealthy>
     *   <thresholdLimit>high</thresholdLimit>
     *   <defaultEncoding>UTF-8</defaultEncoding>
     *   <canRunOnFailed>true</canRunOnFailed>
     *   <useStableBuildAsReference>true</useStableBuildAsReference>
     *   <useDeltaValues>true</useDeltaValues>
     *   <thresholds>
     *     <unstableTotalAll>1</unstableTotalAll>
     *     <unstableTotalHigh>2</unstableTotalHigh>
     *     <unstableTotalNormal>3</unstableTotalNormal>
     *     <unstableTotalLow>4</unstableTotalLow>
     *     <failedTotalAll>5</failedTotalAll>
     *     <failedTotalHigh>6</failedTotalHigh>
     *     <failedTotalNormal>7</failedTotalNormal>
     *     <failedTotalLow>8</failedTotalLow>
     *     <unstableNewAll>9</unstableNewAll>
     *     <unstableNewHigh>10</unstableNewHigh>
     *     <unstableNewNormal>11</unstableNewNormal>
     *     <unstableNewLow>12</unstableNewLow>
     *     <failedNewAll>13</failedNewAll>
     *     <failedNewHigh>14</failedNewHigh>
     *     <failedNewNormal>15</failedNewNormal>
     *     <failedNewLow>16</failedNewLow>
     *   </thresholds>
     *   <shouldDetectModules>true</shouldDetectModules>
     *   <dontComputeNew>false</dontComputeNew>
     *   <doNotResolveRelativePaths>true</doNotResolveRelativePaths>
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
     *   <healthy>3</healthy>
     *   <unHealthy>20</unHealthy>
     *   <thresholdLimit>high</thresholdLimit>
     *   <defaultEncoding>UTF-8</defaultEncoding>
     *   <canRunOnFailed>true</canRunOnFailed>
     *   <useStableBuildAsReference>true</useStableBuildAsReference>
     *   <useDeltaValues>true</useDeltaValues>
     *   <thresholds>
     *     <unstableTotalAll>1</unstableTotalAll>
     *     <unstableTotalHigh>2</unstableTotalHigh>
     *     <unstableTotalNormal>3</unstableTotalNormal>
     *     <unstableTotalLow>4</unstableTotalLow>
     *     <failedTotalAll>5</failedTotalAll>
     *     <failedTotalHigh>6</failedTotalHigh>
     *     <failedTotalNormal>7</failedTotalNormal>
     *     <failedTotalLow>8</failedTotalLow>
     *     <unstableNewAll>9</unstableNewAll>
     *     <unstableNewHigh>10</unstableNewHigh>
     *     <unstableNewNormal>11</unstableNewNormal>
     *     <unstableNewLow>12</unstableNewLow>
     *     <failedNewAll>13</failedNewAll>
     *     <failedNewHigh>14</failedNewHigh>
     *     <failedNewNormal>15</failedNewNormal>
     *     <failedNewLow>16</failedNewLow>
     *   </thresholds>
     *   <shouldDetectModules>true</shouldDetectModules>
     *   <dontComputeNew>false</dontComputeNew>
     *   <doNotResolveRelativePaths>true</doNotResolveRelativePaths>
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
     *   <healthy>3</healthy>
     *   <unHealthy>20</unHealthy>
     *   <thresholdLimit>high</thresholdLimit>
     *   <defaultEncoding>UTF-8</defaultEncoding>
     *   <canRunOnFailed>true</canRunOnFailed>
     *   <useStableBuildAsReference>true</useStableBuildAsReference>
     *   <useDeltaValues>true</useDeltaValues>
     *   <thresholds>
     *     <unstableTotalAll>1</unstableTotalAll>
     *     <unstableTotalHigh>2</unstableTotalHigh>
     *     <unstableTotalNormal>3</unstableTotalNormal>
     *     <unstableTotalLow>4</unstableTotalLow>
     *     <failedTotalAll>5</failedTotalAll>
     *     <failedTotalHigh>6</failedTotalHigh>
     *     <failedTotalNormal>7</failedTotalNormal>
     *     <failedTotalLow>8</failedTotalLow>
     *     <unstableNewAll>9</unstableNewAll>
     *     <unstableNewHigh>10</unstableNewHigh>
     *     <unstableNewNormal>11</unstableNewNormal>
     *     <unstableNewLow>12</unstableNewLow>
     *     <failedNewAll>13</failedNewAll>
     *     <failedNewHigh>14</failedNewHigh>
     *     <failedNewNormal>15</failedNewNormal>
     *     <failedNewLow>16</failedNewLow>
     *   </thresholds>
     *   <shouldDetectModules>true</shouldDetectModules>
     *   <dontComputeNew>false</dontComputeNew>
     *   <doNotResolveRelativePaths>true</doNotResolveRelativePaths>
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
     *   <healthy>3</healthy>
     *   <unHealthy>20</unHealthy>
     *   <thresholdLimit>high</thresholdLimit>
     *   <defaultEncoding>UTF-8</defaultEncoding>
     *   <canRunOnFailed>true</canRunOnFailed>
     *   <useStableBuildAsReference>true</useStableBuildAsReference>
     *   <useDeltaValues>true</useDeltaValues>
     *   <thresholds>
     *     <unstableTotalAll>1</unstableTotalAll>
     *     <unstableTotalHigh>2</unstableTotalHigh>
     *     <unstableTotalNormal>3</unstableTotalNormal>
     *     <unstableTotalLow>4</unstableTotalLow>
     *     <failedTotalAll>5</failedTotalAll>
     *     <failedTotalHigh>6</failedTotalHigh>
     *     <failedTotalNormal>7</failedTotalNormal>
     *     <failedTotalLow>8</failedTotalLow>
     *     <unstableNewAll>9</unstableNewAll>
     *     <unstableNewHigh>10</unstableNewHigh>
     *     <unstableNewNormal>11</unstableNewNormal>
     *     <unstableNewLow>12</unstableNewLow>
     *     <failedNewAll>13</failedNewAll>
     *     <failedNewHigh>14</failedNewHigh>
     *     <failedNewNormal>15</failedNewNormal>
     *     <failedNewLow>16</failedNewLow>
     *   </thresholds>
     *   <shouldDetectModules>true</shouldDetectModules>
     *   <dontComputeNew>false</dontComputeNew>
     *   <doNotResolveRelativePaths>false</doNotResolveRelativePaths>
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
        nodeBuilder.healthy(context.healthy)
        nodeBuilder.unHealthy(context.unHealthy)
        nodeBuilder.thresholdLimit(context.thresholdLimit)
        nodeBuilder.defaultEncoding(context.defaultEncoding)
        nodeBuilder.canRunOnFailed(context.canRunOnFailed)
        nodeBuilder.useStableBuildAsReference(context.useStableBuildAsReference)
        nodeBuilder.useDeltaValues(context.useDeltaValues)
        nodeBuilder.thresholds {
            context.thresholdMap.each { threshold, values ->
                values.each { value, num ->
                    nodeBuilder."${threshold}${value.capitalize()}"(num)
                }
            }
        }
        nodeBuilder.shouldDetectModules(context.shouldDetectModules)
        nodeBuilder.dontComputeNew(context.dontComputeNew)
        nodeBuilder.doNotResolveRelativePaths(context.doNotResolveRelativePaths)
    }

    private def addStaticAnalysisPattern(nodeBuilder, String pattern) {
        nodeBuilder.pattern(pattern)
    }

    private def addStaticAnalysisContextAndPattern(nodeBuilder, StaticAnalysisContext context, String pattern) {
        addStaticAnalysisContext(nodeBuilder, context)
        addStaticAnalysisPattern(nodeBuilder, pattern)
    }

}

