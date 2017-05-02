package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import javaposse.jobdsl.dsl.jobs.MatrixJob
import spock.lang.Specification

import static javaposse.jobdsl.dsl.helpers.publisher.ArchiveXUnitContext.ThresholdMode
import static javaposse.jobdsl.dsl.helpers.publisher.PublisherContext.Behavior.MarkUnstable
import static javaposse.jobdsl.dsl.helpers.publisher.WeblogicDeployerContext.WeblogicDeploymentStageModes

class PublisherContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = new FreeStyleJob(jobManagement, 'test')
    PublisherContext context = new PublisherContext(jobManagement, item)

    def 'call extendedEmail with no options'() {
        when:
        context.extendedEmail {
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.emailext.ExtendedEmailPublisher'
            children().size() == 13
            recipientList[0].value() == '$DEFAULT_RECIPIENTS'
            contentType[0].value() == 'default'
            defaultSubject[0].value() == '$DEFAULT_SUBJECT'
            defaultContent[0].value() == '$DEFAULT_CONTENT'
            attachmentsPattern[0].value().empty
            presendScript[0].value() == '$DEFAULT_PRESEND_SCRIPT'
            classpath[0].value().empty
            attachBuildLog[0].value() == false
            compressBuildLog[0].value() == false
            replyTo[0].value() == '$DEFAULT_REPLYTO'
            saveOutput[0].value() == false
            disabled[0].value() == false
            with(configuredTriggers[0]) {
                children().size() == 1
                with(children()[0]) {
                    name() == 'hudson.plugins.emailext.plugins.trigger.FailureTrigger'
                    children().size() == 1
                    with(email[0]) {
                        children().size() == 9
                        recipientList[0].value().empty
                        subject[0].value() == '$PROJECT_DEFAULT_SUBJECT'
                        body[0].value() == '$PROJECT_DEFAULT_CONTENT'
                        with(recipientProviders[0]) {
                            children().size() == 1
                            children()[0].name() == 'hudson.plugins.emailext.plugins.recipients.ListRecipientProvider'
                        }
                        attachmentsPattern[0].value().empty
                        attachBuildLog[0].value() == false
                        compressBuildLog[0].value() == false
                        replyTo[0].value() == '$PROJECT_DEFAULT_REPLYTO'
                        contentType[0].value() == 'project'
                    }
                }
            }
        }
    }

    def 'call extendedEmail with invalid content type'() {
        when:
        context.extendedEmail {
            contentType(value)
        }

        then:
        thrown(DslScriptException)

        where:
        value << ['', null, 'project']
    }

    def 'call extendedEmail with invalid trigger content type'() {
        when:
        context.extendedEmail {
            triggers {
                failure {
                    contentType(value)
                }
            }
        }

        then:
        thrown(DslScriptException)

        where:
        value << ['', null, 'default']
    }

    def 'call extendedEmail with all options'() {
        when:
        context.extendedEmail {
            recipientList('me@example.org', 'you@example.org')
            recipientList('other@example.org')
            contentType('text/plain')
            defaultSubject('Important')
            defaultContent('read me')
            attachmentPatterns('*.log', '**/report.html')
            attachmentPatterns('foo.txt')
            preSendScript('a script')
            additionalGroovyClasspath('foo.jar', 'bar.jar')
            additionalGroovyClasspath('my.jar')
            attachBuildLog()
            compressBuildLog()
            replyToList('someone@example.org')
            replyToList('test@example.org', 'ci@example.org')
            saveToWorkspace()
            disabled()
            triggers {
                "${trigger}" {
                    sendTo {
                        culprits()
                        developers()
                        recipientList()
                        requester()
                        failingTestSuspects()
                        firstFailingBuildSuspects()
                        upstreamCommitter()
                    }
                    recipientList('test@example.org', 'foo@example.org')
                    recipientList('lala@example.org')
                    contentType('text/html')
                    subject('Lorem')
                    content('Ipsum')
                    attachmentPatterns('test*.html', '*.xml')
                    attachmentPatterns('hello.txt')
                    attachBuildLog()
                    compressBuildLog()
                    replyToList('one@example.org')
                    replyToList('two@example.org', 'three@example.org')
                }
            }
            configure {
                it / foo('bar')
            }
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.emailext.ExtendedEmailPublisher'
            children().size() == 14
            recipientList[0].value() == 'me@example.org, you@example.org, other@example.org'
            contentType[0].value() == 'text/plain'
            defaultSubject[0].value() == 'Important'
            defaultContent[0].value() == 'read me'
            attachmentsPattern[0].value() == '*.log, **/report.html, foo.txt'
            presendScript[0].value() == 'a script'
            with(classpath[0]) {
                children().size() == 3
                with(children()[0]) {
                    name() == 'hudson.plugins.emailext.GroovyScriptPath'
                    children().size() == 1
                    path[0].value() == 'foo.jar'
                }
                with(children()[1]) {
                    name() == 'hudson.plugins.emailext.GroovyScriptPath'
                    children().size() == 1
                    path[0].value() == 'bar.jar'
                }
                with(children()[2]) {
                    name() == 'hudson.plugins.emailext.GroovyScriptPath'
                    children().size() == 1
                    path[0].value() == 'my.jar'
                }
            }
            attachBuildLog[0].value() == true
            compressBuildLog[0].value() == true
            replyTo[0].value() == 'someone@example.org, test@example.org, ci@example.org'
            saveOutput[0].value() == true
            disabled[0].value() == true
            with(configuredTriggers[0]) {
                children().size() == 1
                with(children()[0]) {
                    name() == "hudson.plugins.emailext.plugins.trigger.${className}"
                    children().size() == 1
                    with(email[0]) {
                        children().size() == 9
                        recipientList[0].value() == 'test@example.org, foo@example.org, lala@example.org'
                        subject[0].value() == 'Lorem'
                        body[0].value() == 'Ipsum'
                        with(recipientProviders[0]) {
                            children().size() == 7
                            children()[0].name() ==
                                'hudson.plugins.emailext.plugins.recipients.CulpritsRecipientProvider'
                            children()[1].name() ==
                                'hudson.plugins.emailext.plugins.recipients.DevelopersRecipientProvider'
                            children()[2].name() ==
                                'hudson.plugins.emailext.plugins.recipients.ListRecipientProvider'
                            children()[3].name() ==
                                'hudson.plugins.emailext.plugins.recipients.RequesterRecipientProvider'
                            children()[4].name() ==
                                'hudson.plugins.emailext.plugins.recipients.FailingTestSuspectsRecipientProvider'
                            children()[5].name() ==
                                'hudson.plugins.emailext.plugins.recipients.FirstFailingBuildSuspectsRecipientProvider'
                            children()[6].name() ==
                                'hudson.plugins.emailext.plugins.recipients.UpstreamComitterRecipientProvider'
                        }
                        attachmentsPattern[0].value() == 'test*.html, *.xml, hello.txt'
                        attachBuildLog[0].value() == true
                        compressBuildLog[0].value() == true
                        replyTo[0].value() == 'one@example.org, two@example.org, three@example.org'
                        contentType[0].value() == 'text/html'
                    }
                }
            }
            foo[0].value() == 'bar'
        }

        where:
        trigger          || className
        'aborted'        || 'AbortedTrigger'
        'always'         || 'AlwaysTrigger'
        'beforeBuild'    || 'PreBuildTrigger'
        'firstFailure'   || 'FirstFailureTrigger'
        'secondFailure'  || 'SecondFailureTrigger'
        'failure'        || 'FailureTrigger'
        'stillFailing'   || 'StillFailingTrigger'
        'fixed'          || 'FixedTrigger'
        'notBuilt'       || 'NotBuiltTrigger'
        'statusChanged'  || 'StatusChangedTrigger'
        'success'        || 'SuccessTrigger'
        'improvement'    || 'ImprovementTrigger'
        'regression'     || 'RegressionTrigger'
        'unstable'       || 'UnstableTrigger'
        'firstUnstable'  || 'FirstUnstableTrigger'
        'stillUnstable'  || 'StillUnstableTrigger'
        'fixedUnhealthy' || 'FixedUnhealthyTrigger'
    }

    def 'call standard mailer method'() {
        when:
        context.mailer('recipient')

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        Node mailerPublisher = context.publisherNodes[0]
        mailerPublisher.name() == 'hudson.tasks.Mailer'
        mailerPublisher.recipients[0].value() as String == 'recipient'
        mailerPublisher.dontNotifyEveryUnstableBuild[0].value() as Boolean == false
        mailerPublisher.sendToIndividuals[0].value() as Boolean == false
        1 * jobManagement.requirePlugin('mailer')
    }

    def 'call standard mailer method with all args'() {
        when:
        context.mailer('recipient2', true, true)

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        Node mailerPublisher = context.publisherNodes[0]
        mailerPublisher.name() == 'hudson.tasks.Mailer'
        mailerPublisher.recipients[0].value() as String == 'recipient2'
        mailerPublisher.dontNotifyEveryUnstableBuild[0].value() as Boolean == true
        mailerPublisher.sendToIndividuals[0].value() as Boolean == true
        1 * jobManagement.requirePlugin('mailer')
    }

    def 'call archive artifacts with all args'() {
        when:
        context.archiveArtifacts('include/*', 'exclude/*')

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.tasks.ArtifactArchiver'
            children().size() == 6
            artifacts[0].value() == 'include/*'
            excludes[0].value() == 'exclude/*'
            allowEmptyArchive[0].value() == false
            fingerprint[0].value() == false
            onlyIfSuccessful[0].value() == false
            defaultExcludes[0].value() == true
        }
    }

    def 'call archive artifacts least args'() {
        when:
        context.archiveArtifacts('include/*')

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.tasks.ArtifactArchiver'
            children().size() == 5
            artifacts[0].value() == 'include/*'
            allowEmptyArchive[0].value() == false
            fingerprint[0].value() == false
            onlyIfSuccessful[0].value() == false
            defaultExcludes[0].value() == true
        }
    }

    def 'call archive artifacts with closure'() {
        when:
        context.archiveArtifacts {
            pattern('include/*')
            exclude('exclude/*')
            allowEmpty()
            fingerprint()
            onlyIfSuccessful()
            defaultExcludes(false)
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.tasks.ArtifactArchiver'
            children().size() == 6
            artifacts[0].value() == 'include/*'
            excludes[0].value() == 'exclude/*'
            allowEmptyArchive[0].value() == true
            fingerprint[0].value() == true
            onlyIfSuccessful[0].value() == true
            defaultExcludes[0].value() == false
        }
    }

    def 'call archive artifacts with multiple patterns'() {
        when:
        context.archiveArtifacts {
            pattern('include1/*')
            pattern('include2/*')
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.tasks.ArtifactArchiver'
            children().size() == 5
            artifacts[0].value() == 'include1/*,include2/*'
            allowEmptyArchive[0].value() == false
            fingerprint[0].value() == false
            onlyIfSuccessful[0].value() == false
            defaultExcludes[0].value() == true
        }
    }

    def 'call junit archive with all args'() {
        when:
        context.archiveJunit('include/*') {
            allowEmptyResults()
            retainLongStdout()
            healthScaleFactor(1.5)
            testDataPublishers {
                allowClaimingOfFailedTests()
                publishTestAttachments()
                publishTestStabilityData()
                publishFlakyTestsReport()
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.tasks.junit.JUnitResultArchiver'
            children().size() == 5
            testResults[0].value() == 'include/*'
            allowEmptyResults[0].value() == true
            keepLongStdio[0].value() == true
            healthScaleFactor[0].value() == 1.5
            testDataPublishers[0].children().size() == 4
            testDataPublishers[0].'hudson.plugins.claim.ClaimTestDataPublisher'[0] != null
            testDataPublishers[0].'hudson.plugins.junitattachments.AttachmentPublisher'[0] != null
            testDataPublishers[0].'de.esailors.jenkins.teststability.StabilityTestDataPublisher'[0] != null
            testDataPublishers[0].'com.google.jenkins.flakyTestHandler.plugin.JUnitFlakyTestDataPublisher'[0] != null
        }

        1 * jobManagement.requireMinimumPluginVersion('claim', '2.0')
        1 * jobManagement.requireMinimumPluginVersion('junit-attachments', '1.0')
        1 * jobManagement.requireMinimumPluginVersion('test-stability', '1.0')
        1 * jobManagement.requireMinimumPluginVersion('flaky-test-handler', '1.0.0')
        1 * jobManagement.requireMinimumPluginVersion('junit', '1.10')
    }

    def 'call junit archive with minimal args'() {
        when:
        context.archiveJunit('include/*')

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.tasks.junit.JUnitResultArchiver'
            children().size() == 5
            testResults[0].value() == 'include/*'
            keepLongStdio[0].value() == false
            allowEmptyResults[0].value() == false
            healthScaleFactor[0].value() == 1.0
            testDataPublishers[0].children().size() == 0
        }
        1 * jobManagement.requireMinimumPluginVersion('junit', '1.10')
    }

    def 'call archiveXUnit with no args'() {
        when:
        context.archiveXUnit {
        }

        then:
        Node xUnitNode = context.publisherNodes[0]
        xUnitNode.name() == 'xunit'
        xUnitNode.thresholdMode[0].value() == 1
        xUnitNode.extraConfiguration[0].testTimeMargin[0].value() == 3000

        def failedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.FailedThreshold'[0]
        failedThresholds.unstableThreshold[0].value() == 0
        failedThresholds.unstableNewThreshold[0].value() == 0
        failedThresholds.failureThreshold[0].value() == 0
        failedThresholds.failureNewThreshold[0].value() == 0

        def skippedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.SkippedThreshold'[0]
        skippedThresholds.unstableThreshold[0].value() == 0
        skippedThresholds.unstableNewThreshold[0].value() == 0
        skippedThresholds.failureThreshold[0].value() == 0
        skippedThresholds.failureNewThreshold[0].value() == 0

        1 * jobManagement.requirePlugin('xunit')
    }

    def 'call archiveXUnit with some basic args'() {
        when:
        context.archiveXUnit {
            failedThresholds {
                unstable 1
                unstableNew 0
                failure 3
                failureNew 4
            }
            skippedThresholds {
                unstable 7
                failureNew 9
            }
            timeMargin 4000
        }

        then:
        Node xUnitNode = context.publisherNodes[0]
        xUnitNode.name() == 'xunit'
        xUnitNode.thresholdMode[0].value() == 1
        xUnitNode.extraConfiguration[0].testTimeMargin[0].value() == 4000

        def failedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.FailedThreshold'[0]
        failedThresholds.unstableThreshold[0].value() == 1
        failedThresholds.unstableNewThreshold[0].value() == 0
        failedThresholds.failureThreshold[0].value() == 3
        failedThresholds.failureNewThreshold[0].value() == 4

        def skippedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.SkippedThreshold'[0]
        skippedThresholds.unstableThreshold[0].value() == 7
        skippedThresholds.unstableNewThreshold[0].value() == 0
        skippedThresholds.failureThreshold[0].value() == 0
        skippedThresholds.failureNewThreshold[0].value() == 9

        1 * jobManagement.requirePlugin('xunit')
    }

    def 'call archiveXUnit with all basic args'() {
        when:
        context.archiveXUnit {
            failedThresholds {
                unstable 1
                unstableNew 2
                failure 3
                failureNew 4
            }
            skippedThresholds {
                unstable 5
                unstableNew 6
                failure 7
                failureNew 8
            }
            thresholdMode ThresholdMode.PERCENT
            timeMargin 2000
        }

        then:
        Node xUnitNode = context.publisherNodes[0]
        xUnitNode.name() == 'xunit'
        xUnitNode.thresholdMode[0].value() == 2
        xUnitNode.extraConfiguration[0].testTimeMargin[0].value() == 2000

        def failedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.FailedThreshold'[0]
        failedThresholds.unstableThreshold[0].value() == 1
        failedThresholds.unstableNewThreshold[0].value() == 2
        failedThresholds.failureThreshold[0].value() == 3
        failedThresholds.failureNewThreshold[0].value() == 4

        def skippedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.SkippedThreshold'[0]
        skippedThresholds.unstableThreshold[0].value() == 5
        skippedThresholds.unstableNewThreshold[0].value() == 6
        skippedThresholds.failureThreshold[0].value() == 7
        skippedThresholds.failureNewThreshold[0].value() == 8

        1 * jobManagement.requirePlugin('xunit')
    }

    def 'call archiveXUnit with null values'() {
        when:
        context.archiveXUnit {
            failedThresholds {
                unstable(null)
                unstableNew(null)
                failure(null)
                failureNew(null)
            }
            skippedThresholds {
                unstable(null)
                unstableNew(null)
                failure(null)
                failureNew(null)
            }
            thresholdMode(ThresholdMode.PERCENT)
            timeMargin(2000)
        }

        then:
        Node xUnitNode = context.publisherNodes[0]
        xUnitNode.name() == 'xunit'
        xUnitNode.thresholdMode[0].value() == 2
        xUnitNode.extraConfiguration[0].testTimeMargin[0].value() == 2000

        def failedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.FailedThreshold'[0]
        failedThresholds.unstableThreshold[0].value() == ''
        failedThresholds.unstableNewThreshold[0].value() == ''
        failedThresholds.failureThreshold[0].value() == ''
        failedThresholds.failureNewThreshold[0].value() == ''

        def skippedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.SkippedThreshold'[0]
        skippedThresholds.unstableThreshold[0].value() == ''
        skippedThresholds.unstableNewThreshold[0].value() == ''
        skippedThresholds.failureThreshold[0].value() == ''
        skippedThresholds.failureNewThreshold[0].value() == ''

        1 * jobManagement.requirePlugin('xunit')
    }

    def 'call archiveXUnit with all valid thresholdMode values'() {
        when:
        context.archiveXUnit {
            thresholdMode input
        }

        then:
        Node xUnitNode = context.publisherNodes[0]
        xUnitNode.name() == 'xunit'
        xUnitNode.thresholdMode[0].value() == output
        xUnitNode.extraConfiguration[0].testTimeMargin[0].value() == 3000

        def failedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.FailedThreshold'[0]
        failedThresholds.unstableThreshold[0].value() == 0
        failedThresholds.unstableNewThreshold[0].value() == 0
        failedThresholds.failureThreshold[0].value() == 0
        failedThresholds.failureNewThreshold[0].value() == 0

        def skippedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.SkippedThreshold'[0]
        skippedThresholds.unstableThreshold[0].value() == 0
        skippedThresholds.unstableNewThreshold[0].value() == 0
        skippedThresholds.failureThreshold[0].value() == 0
        skippedThresholds.failureNewThreshold[0].value() == 0

        1 * jobManagement.requirePlugin('xunit')

        where:
        input                 | output
        ThresholdMode.NUMBER  | 1
        ThresholdMode.PERCENT | 2
    }

    def 'call archiveXUnit with all result types'() {
        when:
        context.archiveXUnit {
            "${input}" {
                pattern 'some_pattern'
            }
        }

        then:
        Node xUnitNode = context.publisherNodes[0]
        xUnitNode.name() == 'xunit'

        def resultFile = xUnitNode.types[0]."${output}"[0]
        resultFile.pattern[0].value() == 'some_pattern'

        1 * jobManagement.requirePlugin('xunit')

        where:
        input        | output
        'aUnit'      | 'AUnitJunitHudsonTestType'
        'boostTest'  | 'BoostTestJunitHudsonTestType'
        'cTest'      | 'CTestType'
        'check'      | 'CheckType'
        'cppTest'    | 'CppTestJunitHudsonTestType'
        'cppUnit'    | 'CppUnitJunitHudsonTestType'
        'customTool' | 'CustomType'
        'embUnit'    | 'EmbUnitType'
        'fpcUnit'    | 'FPCUnitJunitHudsonTestType'
        'googleTest' | 'GoogleTestType'
        'gtester'    | 'GTesterJunitHudsonTestType'
        'jUnit'      | 'JUnitType'
        'msTest'     | 'MSTestJunitHudsonTestType'
        'mbUnit'     | 'MbUnitType'
        'nUnit'      | 'NUnitJunitHudsonTestType'
        'phpUnit'    | 'PHPUnitJunitHudsonTestType'
        'qTestLib'   | 'QTestLibType'
        'unitTest'   | 'UnitTestJunitHudsonTestType'
        'valgrind'   | 'ValgrindJunitHudsonTestType'
        'xUnitDotNET'   | 'XUnitDotNetTestType'
    }

    def 'call archiveXUnit with combination of most options'() {
        when:
        context.archiveXUnit {
            aUnit {
                pattern 'first_pattern'
            }
            cTest {
                pattern 'second_pattern'
                failIfNotNew false
            }
            cppTest {
                pattern 'third_pattern'
                skipNoTestFiles true
                failIfNotNew false
                deleteOutputFiles false
                stopProcessingIfError false
            }
            customTool {
                pattern 'fourth_pattern'
                stylesheet 'XSL_pattern'
            }
            fpcUnit {
                pattern 'fifth_pattern'
                skipNoTestFiles true
            }
            fpcUnit {
                pattern 'sixth_pattern'
                stopProcessingIfError false
            }
            failedThresholds {
                unstable 1
                unstableNew 2
                failure 3
                failureNew 4
            }
            skippedThresholds {
                unstable 5
                unstableNew 6
                failure 7
                failureNew 8
            }
            thresholdMode ThresholdMode.PERCENT
            timeMargin 2000
        }

        then:
        Node xUnitNode = context.publisherNodes[0]
        xUnitNode.name() == 'xunit'
        xUnitNode.thresholdMode[0].value() == 2
        xUnitNode.extraConfiguration[0].testTimeMargin[0].value() == 2000

        def aUnit = xUnitNode.types[0].AUnitJunitHudsonTestType[0]
        aUnit.pattern[0].value() == 'first_pattern'
        aUnit.skipNoTestFiles[0].value() == false
        aUnit.failIfNotNew[0].value() == true
        aUnit.deleteOutputFiles[0].value() == true
        aUnit.stopProcessingIfError[0].value() == true

        def cTest = xUnitNode.types[0].CTestType[0]
        cTest.pattern[0].value() == 'second_pattern'
        cTest.skipNoTestFiles[0].value() == false
        cTest.failIfNotNew[0].value() == false
        cTest.deleteOutputFiles[0].value() == true
        cTest.stopProcessingIfError[0].value() == true

        def cppTest = xUnitNode.types[0].CppTestJunitHudsonTestType[0]
        cppTest.pattern[0].value() == 'third_pattern'
        cppTest.skipNoTestFiles[0].value() == true
        cppTest.failIfNotNew[0].value() == false
        cppTest.deleteOutputFiles[0].value() == false
        cppTest.stopProcessingIfError[0].value() == false

        def customTool = xUnitNode.types[0].CustomType[0]
        customTool.pattern[0].value() == 'fourth_pattern'
        customTool.skipNoTestFiles[0].value() == false
        customTool.failIfNotNew[0].value() == true
        customTool.deleteOutputFiles[0].value() == true
        customTool.stopProcessingIfError[0].value() == true
        customTool.customXSL[0].value() == 'XSL_pattern'

        def fpcUnit0 = xUnitNode.types[0].FPCUnitJunitHudsonTestType[0]
        fpcUnit0.pattern[0].value() == 'fifth_pattern'
        fpcUnit0.skipNoTestFiles[0].value() == true
        fpcUnit0.failIfNotNew[0].value() == true
        fpcUnit0.deleteOutputFiles[0].value() == true
        fpcUnit0.stopProcessingIfError[0].value() == true

        def fpcUnit1 = xUnitNode.types[0].FPCUnitJunitHudsonTestType[1]
        fpcUnit1.pattern[0].value() == 'sixth_pattern'
        fpcUnit1.skipNoTestFiles[0].value() == false
        fpcUnit1.failIfNotNew[0].value() == true
        fpcUnit1.deleteOutputFiles[0].value() == true
        fpcUnit1.stopProcessingIfError[0].value() == false

        def failedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.FailedThreshold'[0]
        failedThresholds.unstableThreshold[0].value() == 1
        failedThresholds.unstableNewThreshold[0].value() == 2
        failedThresholds.failureThreshold[0].value() == 3
        failedThresholds.failureNewThreshold[0].value() == 4

        def skippedThresholds = xUnitNode.thresholds[0].'org.jenkinsci.plugins.xunit.threshold.SkippedThreshold'[0]
        skippedThresholds.unstableThreshold[0].value() == 5
        skippedThresholds.unstableNewThreshold[0].value() == 6
        skippedThresholds.failureThreshold[0].value() == 7
        skippedThresholds.failureNewThreshold[0].value() == 8

        1 * jobManagement.requirePlugin('xunit')
    }

    def 'call testng archive with all args'() {
        when:
        context.archiveTestNG('include/*') {
            escapeTestDescription(false)
            escapeExceptionMessages(false)
            showFailedBuildsInTrendGraph(true)
            markBuildAsUnstableOnSkippedTests(true)
            markBuildAsFailureOnFailedConfiguration(true)
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.testng.Publisher'
            children().size() == 6
            reportFilenamePattern[0].value() == 'include/*'
            escapeTestDescp[0].value() == false
            escapeExceptionMsg[0].value() == false
            showFailedBuilds[0].value() == true
            unstableOnSkippedTests[0].value() == true
            failureOnFailedTestConfig[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('testng-plugin', '1.10')
    }

    def 'call testng archive with minimal args'() {
        when:
        context.archiveTestNG()

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.testng.Publisher'
            children().size() == 6
            reportFilenamePattern[0].value() == '**/testng-results.xml'
            escapeTestDescp[0].value() == true
            escapeExceptionMsg[0].value() == true
            showFailedBuilds[0].value() == false
            unstableOnSkippedTests[0].value() == false
            failureOnFailedTestConfig[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('testng-plugin', '1.10')
    }

    def 'calling gatling archive with minimal args'() {
        when:
        context.archiveGatling()

        then:
        with(context.publisherNodes[0]) {
            name() == 'io.gatling.jenkins.GatlingPublisher'
            children().size == 1
            enabled[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('gatling', '1.1.1')
    }

    def 'calling gatling archive with all args'() {
        when:
        context.archiveGatling {
            enabled(false)
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'io.gatling.jenkins.GatlingPublisher'
            children().size == 1
            enabled[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('gatling', '1.1.1')
    }

    def 'call jacoco code coverage with no args'() {
        when:
        context.jacocoCodeCoverage()

        then:
        Node jacocoNode = context.publisherNodes[0]
        jacocoNode.name() == 'hudson.plugins.jacoco.JacocoPublisher'
        jacocoNode.execPattern[0].value() == '**/target/**.exec'
        jacocoNode.minimumInstructionCoverage[0].value() == '0'
        jacocoNode.changeBuildStatus[0] == null
        1 * jobManagement.requirePlugin('jacoco')
    }

    def 'call jacoco code coverage with closure, set changeBuildStatus'(change) {
        when:
        context.jacocoCodeCoverage {
            changeBuildStatus(change)
        }

        then:
        Node jacocoNode = context.publisherNodes[0]
        jacocoNode.name() == 'hudson.plugins.jacoco.JacocoPublisher'
        jacocoNode.changeBuildStatus[0].value() == change ? 'true' : 'false'
        1 * jobManagement.requirePlugin('jacoco')
        1 * jobManagement.requireMinimumPluginVersion('jacoco', '1.0.13')

        where:
        change << [true, false]
    }

    def 'call jacoco code coverage with closure, changeBuildStatus with no args defaults to true'() {
        when:
        context.jacocoCodeCoverage {
            changeBuildStatus()
        }

        then:
        Node jacocoNode = context.publisherNodes[0]
        jacocoNode.name() == 'hudson.plugins.jacoco.JacocoPublisher'
        jacocoNode.execPattern[0].value() == '**/target/**.exec'
        jacocoNode.minimumInstructionCoverage[0].value() == '0'
        jacocoNode.changeBuildStatus[0].value() == true
        1 * jobManagement.requirePlugin('jacoco')
        1 * jobManagement.requireMinimumPluginVersion('jacoco', '1.0.13')
    }

    def 'call jacoco code coverage with all args'() {
        when:
        context.jacocoCodeCoverage {
            execPattern 'execfiles'
            classPattern 'classdir'
            sourcePattern 'sourcedir'
            inclusionPattern 'inclusiondir'
            exclusionPattern 'exclusiondir'
            minimumInstructionCoverage '1'
            minimumBranchCoverage '2'
            minimumComplexityCoverage '3'
            minimumLineCoverage '4'
            minimumMethodCoverage '5'
            minimumClassCoverage '6'
            maximumInstructionCoverage '7'
            maximumBranchCoverage '8'
            maximumComplexityCoverage '9'
            maximumLineCoverage '10'
            maximumMethodCoverage '11'
            maximumClassCoverage '12'
            changeBuildStatus true
        }

        then:
        Node jacocoNode = context.publisherNodes[0]
        jacocoNode.name() == 'hudson.plugins.jacoco.JacocoPublisher'
        jacocoNode.execPattern[0].value() == 'execfiles'
        jacocoNode.classPattern[0].value() == 'classdir'
        jacocoNode.sourcePattern[0].value() == 'sourcedir'
        jacocoNode.inclusionPattern[0].value() == 'inclusiondir'
        jacocoNode.exclusionPattern[0].value() == 'exclusiondir'
        jacocoNode.minimumInstructionCoverage[0].value() == '1'
        jacocoNode.minimumBranchCoverage[0].value() == '2'
        jacocoNode.minimumComplexityCoverage[0].value() == '3'
        jacocoNode.minimumLineCoverage[0].value() == '4'
        jacocoNode.minimumMethodCoverage[0].value() == '5'
        jacocoNode.minimumClassCoverage[0].value() == '6'
        jacocoNode.maximumInstructionCoverage[0].value() == '7'
        jacocoNode.maximumBranchCoverage[0].value() == '8'
        jacocoNode.maximumComplexityCoverage[0].value() == '9'
        jacocoNode.maximumLineCoverage[0].value() == '10'
        jacocoNode.maximumMethodCoverage[0].value() == '11'
        jacocoNode.maximumClassCoverage[0].value() == '12'
        jacocoNode.changeBuildStatus[0].value() == true
        1 * jobManagement.requirePlugin('jacoco')
        1 * jobManagement.requireMinimumPluginVersion('jacoco', '1.0.13')
    }

    def 'calling minimal html publisher closure'() {
        when:
        context.publishHtml {
            report('build/*') {
            }
        }

        then:
        Node publisherHtmlNode = context.publisherNodes[0]
        publisherHtmlNode.name() == 'htmlpublisher.HtmlPublisher'
        !publisherHtmlNode.reportTargets.isEmpty()
        def target = publisherHtmlNode.reportTargets[0].'htmlpublisher.HtmlPublisherTarget'[0]
        target.children().size() == 6
        target.reportName[0].value() == ''
        target.reportDir[0].value() == 'build/*'
        target.reportFiles[0].value() == 'index.html'
        target.keepAll[0].value() == false
        target.allowMissing[0].value() == false
        target.alwaysLinkToLastBuild[0].value() == false
        1 * jobManagement.requireMinimumPluginVersion('htmlpublisher', '1.5')
    }

    def 'calling html publisher closure with all options'() {
        when:
        context.publishHtml {
            report('build/*') {
                reportName('foo')
                reportFiles('test.html')
                allowMissing()
                keepAll()
                alwaysLinkToLastBuild()
            }
        }

        then:
        Node publisherHtmlNode = context.publisherNodes[0]
        publisherHtmlNode.name() == 'htmlpublisher.HtmlPublisher'
        !publisherHtmlNode.reportTargets.isEmpty()
        def target = publisherHtmlNode.reportTargets[0].'htmlpublisher.HtmlPublisherTarget'[0]
        target.children().size() == 6
        target.reportName[0].value() == 'foo'
        target.reportDir[0].value() == 'build/*'
        target.reportFiles[0].value() == 'test.html'
        target.keepAll[0].value() == true
        target.allowMissing[0].value() == true
        target.alwaysLinkToLastBuild[0].value() == true
        1 * jobManagement.requireMinimumPluginVersion('htmlpublisher', '1.5')
    }

    def 'calling html publisher with multiple reports'() {
        when:
        context.publishHtml {
            report('build/*') {
                reportName('Build Report')
            }
            report('test/*') {
                reportName('Test Report')
            }
        }

        then:
        Node publisherHtmlNode = context.publisherNodes[0]
        publisherHtmlNode.name() == 'htmlpublisher.HtmlPublisher'
        !publisherHtmlNode.reportTargets.isEmpty()
        def target1 = publisherHtmlNode.reportTargets[0].'htmlpublisher.HtmlPublisherTarget'[0]
        target1.reportName[0].value() == 'Build Report'
        target1.reportDir[0].value() == 'build/*'

        def target2 = publisherHtmlNode.reportTargets[0].'htmlpublisher.HtmlPublisherTarget'[1]
        target2.reportName[0].value() == 'Test Report'
        target2.reportDir[0].value() == 'test/*'

        1 * jobManagement.requireMinimumPluginVersion('htmlpublisher', '1.5')
    }

    def 'call Jabber publish with minimal args'() {
        when:
        context.publishJabber('me@gmail.com')

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jabber.im.transport.JabberPublisher'
            children().size() == 9
            with(targets[0]) {
                children().size() == 1
                with(children()[0]) {
                    name() == 'hudson.plugins.im.DefaultIMMessageTarget'
                    children().size() == 1
                    value[0].value() == 'me@gmail.com'
                }
            }
            strategy[0].value() == 'ALL'
            notifyOnBuildStart[0].value() == false
            notifySuspects[0].value() == false
            notifyCulprits[0].value() == false
            notifyFixers[0].value() == false
            notifyUpstreamCommitters[0].value() == false
            buildToChatNotifier[0].attribute('class') == 'hudson.plugins.im.build_notify.DefaultBuildToChatNotifier'
            matrixMultiplier[0].value() == 'ONLY_CONFIGURATIONS'
        }
        1 * jobManagement.requireMinimumPluginVersion('jabber', '1.35')
    }

    def 'call Jabber publish with group chat'() {
        when:
        context.publishJabber('*@gmail.com')

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jabber.im.transport.JabberPublisher'
            children().size() == 9
            with(targets[0]) {
                children().size() == 1
                with(children()[0]) {
                    name() == 'hudson.plugins.im.GroupChatIMMessageTarget'
                    children().size() == 2
                    name[0].value() == '@gmail.com'
                    notificationOnly[0].value() == false
                }
            }
            strategy[0].value() == 'ALL'
            notifyOnBuildStart[0].value() == false
            notifySuspects[0].value() == false
            notifyCulprits[0].value() == false
            notifyFixers[0].value() == false
            notifyUpstreamCommitters[0].value() == false
            buildToChatNotifier[0].attribute('class') == 'hudson.plugins.im.build_notify.DefaultBuildToChatNotifier'
            matrixMultiplier[0].value() == 'ONLY_CONFIGURATIONS'
        }
        1 * jobManagement.requireMinimumPluginVersion('jabber', '1.35')
    }

    def 'call Jabber publish with conference room'() {
        when:
        context.publishJabber('room@conference.gmail.com')

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jabber.im.transport.JabberPublisher'
            children().size() == 9
            with(targets[0]) {
                children().size() == 1
                with(children()[0]) {
                    name() == 'hudson.plugins.im.GroupChatIMMessageTarget'
                    children().size() == 2
                    name[0].value() == 'room@conference.gmail.com'
                    notificationOnly[0].value() == false
                }
            }
            strategy[0].value() == 'ALL'
            notifyOnBuildStart[0].value() == false
            notifySuspects[0].value() == false
            notifyCulprits[0].value() == false
            notifyFixers[0].value() == false
            notifyUpstreamCommitters[0].value() == false
            buildToChatNotifier[0].attribute('class') == 'hudson.plugins.im.build_notify.DefaultBuildToChatNotifier'
            matrixMultiplier[0].value() == 'ONLY_CONFIGURATIONS'
        }
        1 * jobManagement.requireMinimumPluginVersion('jabber', '1.35')
    }

    def 'call Jabber publish with closure args'() {
        when:
        context.publishJabber('me@gmail.com') {
            strategyName('FAILURE_AND_FIXED')
            notifyOnBuildStart()
            notifySuspects()
            notifyCulprits()
            notifyFixers()
            notifyUpstreamCommitters()
            channelNotificationName('PrintFailingTests')
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jabber.im.transport.JabberPublisher'
            children().size() == 9
            with(targets[0]) {
                children().size() == 1
                with(children()[0]) {
                    name() == 'hudson.plugins.im.DefaultIMMessageTarget'
                    children().size() == 1
                    value[0].value() == 'me@gmail.com'
                }
            }
            strategy[0].value() == 'FAILURE_AND_FIXED'
            notifyOnBuildStart[0].value() == true
            notifySuspects[0].value() == true
            notifyCulprits[0].value() == true
            notifyFixers[0].value() == true
            notifyUpstreamCommitters[0].value() == true
            buildToChatNotifier[0].attribute('class') ==
                    'hudson.plugins.im.build_notify.PrintFailingTestsBuildToChatNotifier'
            matrixMultiplier[0].value() == 'ONLY_CONFIGURATIONS'
        }
        1 * jobManagement.requireMinimumPluginVersion('jabber', '1.35')
    }

    def 'call Jabber publish with NEW_FAILURE_AND_FIXED strategy'() {
        when:
        context.publishJabber('me@gmail.com') {
            strategyName('NEW_FAILURE_AND_FIXED')
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jabber.im.transport.JabberPublisher'
            children().size() == 9
            with(targets[0]) {
                children().size() == 1
                with(children()[0]) {
                    name() == 'hudson.plugins.im.DefaultIMMessageTarget'
                    children().size() == 1
                    value[0].value() == 'me@gmail.com'
                }
            }
            strategy[0].value() == 'NEW_FAILURE_AND_FIXED'
            notifyOnBuildStart[0].value() == false
            notifySuspects[0].value() == false
            notifyCulprits[0].value() == false
            notifyFixers[0].value() == false
            notifyUpstreamCommitters[0].value() == false
            buildToChatNotifier[0].attribute('class') == 'hudson.plugins.im.build_notify.DefaultBuildToChatNotifier'
            matrixMultiplier[0].value() == 'ONLY_CONFIGURATIONS'
        }
        1 * jobManagement.requireMinimumPluginVersion('jabber', '1.35')
        1 * jobManagement.requireMinimumPluginVersion('instant-messaging', '1.26')
    }

    def 'call Jabber publish with invalid strategy'() {
        when:
        context.publishJabber('me@gmail.com') {
            strategyName(strategy)
        }

        then:
        thrown(DslScriptException)

        where:
        strategy << [null, '', 'NOPE']
    }

    def 'call Jabber publish with invalid channel notification name'() {
        when:
        context.publishJabber('me@gmail.com') {
            channelNotificationName(name)
        }

        then:
        thrown(DslScriptException)

        where:
        name << [null, '', 'NOPE']
    }

    def 'call Clone Workspace publish with minimal args'() {
        when:
        context.publishCloneWorkspace('*/**')

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.cloneworkspace.CloneWorkspacePublisher'
        publisherNode.workspaceGlob[0].value() == '*/**'
        publisherNode.workspaceExcludeGlob[0].value() == ''
        publisherNode.criteria[0].value() == 'Any'
        publisherNode.archiveMethod[0].value() == 'TAR'
        publisherNode.overrideDefaultExcludes[0].value() == false
        1 * jobManagement.requirePlugin('clone-workspace-scm')
    }

    def 'call Clone Workspace publish with all args'() {
        when:
        context.publishCloneWorkspace('*/**', '*/.svn', 'Not Failed', 'ZIP', true)

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.cloneworkspace.CloneWorkspacePublisher'
        publisherNode.workspaceGlob[0].value() == '*/**'
        publisherNode.workspaceExcludeGlob[0].value() == '*/.svn'
        publisherNode.criteria[0].value() == 'Not Failed'
        publisherNode.archiveMethod[0].value() == 'ZIP'
        publisherNode.overrideDefaultExcludes[0].value() == true
        1 * jobManagement.requirePlugin('clone-workspace-scm')
    }

    def 'call Clone Workspace publish to get exceptions'() {
        when:
        context.publishCloneWorkspace('*/**', '*/.svn', 'Quite plainly wrong', 'ZIP', true)

        then:
        thrown(DslScriptException)

        when:
        context.publishCloneWorkspace('*/**', '*/.svn', 'Not Failed', 'ZAP', true)

        then:
        thrown(DslScriptException)
    }

    def 'call Clone Workspace with Closure'() {
        when:
        context.publishCloneWorkspace('*/**') {
            criteria 'Not Failed'
            archiveMethod 'ZIP'
            workspaceExcludeGlob '*/.svn'
            overrideDefaultExcludes true
        }

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.cloneworkspace.CloneWorkspacePublisher'
        publisherNode.workspaceGlob[0].value() == '*/**'
        publisherNode.workspaceExcludeGlob[0].value() == '*/.svn'
        publisherNode.criteria[0].value() == 'Not Failed'
        publisherNode.archiveMethod[0].value() == 'ZIP'
        publisherNode.overrideDefaultExcludes[0].value() == true
        (1.._) * jobManagement.requirePlugin('clone-workspace-scm')
    }

    def 'call scp publish with not enough entries'() {
        when:
        context.publishScp('javadoc', null)

        then:
        thrown(DslScriptException)
    }

    def 'call scp publish with closure'() {
        when:
        context.publishScp('javadoc') {
            entry('api-sdk/**/*')
        }

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'be.certipost.hudson.plugin.SCPRepositoryPublisher'
        publisherNode.siteName[0].value() == 'javadoc'
        def entryNode = publisherNode.entries[0].'be.certipost.hudson.plugin.Entry'[0]
        entryNode.filePath[0].value() == ''
        entryNode.sourceFile[0].value() == 'api-sdk/**/*'
        entryNode.keepHierarchy[0].value() == 'false'
        1 * jobManagement.requirePlugin('scp')

        when:
        context.publishScp('javadoc') {
            entry('build/javadocs/**/*', 'javadoc', true)
        }

        then:
        Node publisherNode2 = context.publisherNodes[1]
        publisherNode2.name() == 'be.certipost.hudson.plugin.SCPRepositoryPublisher'
        publisherNode2.siteName[0].value() == 'javadoc'
        def entryNode2 = publisherNode2.entries[0].'be.certipost.hudson.plugin.Entry'[0]
        entryNode2.filePath[0].value() == 'javadoc'
        entryNode2.sourceFile[0].value() == 'build/javadocs/**/*'
        entryNode2.keepHierarchy[0].value() == 'true'
        1 * jobManagement.requirePlugin('scp')
    }

    def 'call scp publish with collection of sources'() {
        when:
        context.publishScp('javadoc') {
            entries(['api-sdk/**/*', 'docs/**/*'])
        }

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'be.certipost.hudson.plugin.SCPRepositoryPublisher'
        publisherNode.siteName[0].value() == 'javadoc'
        publisherNode.entries[0].children().size() == 2
        with(publisherNode.entries[0].'be.certipost.hudson.plugin.Entry'[0]) {
            filePath[0].value() == ''
            sourceFile[0].value() == 'api-sdk/**/*'
            keepHierarchy[0].value() == 'false'
        }
        with(publisherNode.entries[0].'be.certipost.hudson.plugin.Entry'[1]) {
            filePath[0].value() == ''
            sourceFile[0].value() == 'docs/**/*'
            keepHierarchy[0].value() == 'false'
        }
        1 * jobManagement.requirePlugin('scp')

        when:
        context.publishScp('javadoc') {
            entries(['build/javadocs/**/*', 'build/groovydoc/**/*'], 'javadoc', true)
        }

        then:
        Node publisherNode2 = context.publisherNodes[1]
        publisherNode2.name() == 'be.certipost.hudson.plugin.SCPRepositoryPublisher'
        publisherNode2.siteName[0].value() == 'javadoc'
        publisherNode2.entries[0].children().size() == 2
        with(publisherNode2.entries[0].'be.certipost.hudson.plugin.Entry'[0]) {
            filePath[0].value() == 'javadoc'
            sourceFile[0].value() == 'build/javadocs/**/*'
            keepHierarchy[0].value() == 'true'
        }
        with(publisherNode2.entries[0].'be.certipost.hudson.plugin.Entry'[1]) {
            filePath[0].value() == 'javadoc'
            sourceFile[0].value() == 'build/groovydoc/**/*'
            keepHierarchy[0].value() == 'true'
        }
        1 * jobManagement.requirePlugin('scp')
    }

    def 'call trigger downstream without args'() {
        when:
        context.downstream('THE-JOB')

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.tasks.BuildTrigger'
        publisherNode.childProjects[0].value() == 'THE-JOB'
        publisherNode.threshold[0].name[0].value() == 'SUCCESS'
        publisherNode.threshold[0].ordinal[0].value() == 0
        publisherNode.threshold[0].color[0].value() == 'BLUE'
    }

    def 'call trigger downstream with project list'() {
        when:
        context.downstream(['job1', 'job2'])

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.tasks.BuildTrigger'
        publisherNode.childProjects[0].value() == 'job1, job2'
        publisherNode.threshold[0].name[0].value() == 'SUCCESS'
        publisherNode.threshold[0].ordinal[0].value() == 0
        publisherNode.threshold[0].color[0].value() == 'BLUE'
    }

    def 'call trigger downstream'() {
        when:
        context.downstream('THE-JOB', 'FAILURE')

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.tasks.BuildTrigger'
        publisherNode.childProjects[0].value() == 'THE-JOB'
        publisherNode.threshold[0].name[0].value() == 'FAILURE'
        publisherNode.threshold[0].ordinal[0].value() == 2
        publisherNode.threshold[0].color[0].value() == 'RED'
    }

    def 'call trigger downstream with bad args'() {
        when:
        context.downstream('THE-JOB', 'BAD')

        then:
        thrown(DslScriptException)
    }

    def 'call downstream ext with all args'() {
        when:
        context.downstreamParameterized {
            trigger('Project1, Project2') {
                condition('UNSTABLE_OR_BETTER')
                triggerWithNoParameters()
                parameters {
                    currentBuild()
                    propertiesFile('dir/my.properties')
                    gitRevision(false)
                    predefinedProp('key1', 'value1')
                    predefinedProps([key2: 'value2', key3: 'value3'])
                    matrixSubset('label=="${TARGET}"')
                    subversionRevision()
                    booleanParam('aParam')
                    booleanParam('bParam', false)
                    booleanParam('cParam', true)
                    sameNode()
                    nodeLabel('nodeParam', 'node_label')
                }
            }
            trigger('Project2') {
                parameters {
                    currentBuild()
                }
            }
        }

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.parameterizedtrigger.BuildTrigger'
        publisherNode.configs[0].children().size() == 2
        with(publisherNode.configs[0].'hudson.plugins.parameterizedtrigger.BuildTriggerConfig'[0]) {
            projects[0].value() == 'Project1, Project2'
            condition[0].value() == 'UNSTABLE_OR_BETTER'
            triggerWithNoParameters[0].value() == true
            configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'[0] instanceof Node
            configs[0].'hudson.plugins.parameterizedtrigger.FileBuildParameters'[0].propertiesFile[0].value() ==
                    'dir/my.properties'
            configs[0].'hudson.plugins.git.GitRevisionBuildParameters'[0].combineQueuedCommits[0].value() == false
            configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'.size() == 1
            configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'[0].'properties'[0].value() ==
                    'key1=value1\nkey2=value2\nkey3=value3'
            configs[0].'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'[0].filter[0].value() ==
                    'label=="${TARGET}"'
            configs[0].'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'[0] instanceof Node
            block.size() == 0

            def boolParams = configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameters'[0].configs[0]
            boolParams.children().size() == 3
            def boolNode = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[0]
            boolNode.name[0].value() == 'aParam'
            boolNode.value[0].value() == false
            def boolNode1 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[1]
            boolNode1.name[0].value() == 'bParam'
            boolNode1.value[0].value() == false
            def boolNode2 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[2]
            boolNode2.name[0].value() == 'cParam'
            boolNode2.value[0].value() == true

            def nodeNode = configs[0].'hudson.plugins.parameterizedtrigger.NodeParameters'[0]
            nodeNode != null

            def nodeLabel = configs[0].
            'org.jvnet.jenkins.plugins.nodelabelparameter.parameterizedtrigger.NodeLabelBuildParameter'[0]
            nodeLabel.name[0].value() == 'nodeParam'
            nodeLabel.nodeLabel[0].value() == 'node_label'

            block.isEmpty()
        }

        Node second = publisherNode.configs[0].'hudson.plugins.parameterizedtrigger.BuildTriggerConfig'[1]
        second.projects[0].value() == 'Project2'
        second.condition[0].value() == 'SUCCESS'
        second.triggerWithNoParameters[0].value() == false
        second.configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'[0] instanceof Node

        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
        1 * jobManagement.requireMinimumPluginVersion('git', '2.5.3')

        when:
        context.downstreamParameterized {
            trigger('Project3') {
            }
        }

        then:
        Node third = context.publisherNodes[1].configs[0].'hudson.plugins.parameterizedtrigger.BuildTriggerConfig'[0]
        third.projects[0].value() == 'Project3'
        third.condition[0].value() == 'SUCCESS'
        third.triggerWithNoParameters[0].value() == false
        third.configs[0].attribute('class') == 'java.util.Collections$EmptyList'
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
    }

    def 'call parametrized downstream with project list'() {
        when:
        context.downstreamParameterized {
            trigger(['Project1', 'Project2']) {
            }
        }

        then:
        Node third = context.publisherNodes[0].configs[0].'hudson.plugins.parameterizedtrigger.BuildTriggerConfig'[0]
        third.projects[0].value() == 'Project1, Project2'
        third.condition[0].value() == 'SUCCESS'
        third.triggerWithNoParameters[0].value() == false
        third.configs[0].attribute('class') == 'java.util.Collections$EmptyList'
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
    }

    def 'call parametrized downstream with FAILED_OR_BETTER condition'() {
        when:
        context.downstreamParameterized {
            trigger('Project1') {
                condition('FAILED_OR_BETTER')
            }
        }

        then:
        Node third = context.publisherNodes[0].configs[0].'hudson.plugins.parameterizedtrigger.BuildTriggerConfig'[0]
        third.projects[0].value() == 'Project1'
        third.condition[0].value() == 'FAILED_OR_BETTER'
        third.triggerWithNoParameters[0].value() == false
        third.configs[0].attribute('class') == 'java.util.Collections$EmptyList'
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
    }

    def 'call violations plugin with no args has correct defaults'() {
        when:
        context.violations()

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.config[0].limit[0].value() == '100'
        publisherNode.config[0].sourcePathPattern[0].value() == ''
        publisherNode.config[0].fauxProjectPath[0].value() == ''
        publisherNode.config[0].encoding[0].value() == 'default'
        def typeConfigsNode = publisherNode.config[0].typeConfigs[0]
        typeConfigsNode.entry.size() == 17
        def simianNode = typeConfigsNode.entry.find { it.string[0].value() == 'simian' }
        simianNode != null
        def typeConfigNode = simianNode.'hudson.plugins.violations.TypeConfig'[0]
        typeConfigNode.type[0].value() == 'simian'
        typeConfigNode.min[0].value() == '10'
        typeConfigNode.max[0].value() == '999'
        typeConfigNode.unstable[0].value() == '999'
        typeConfigNode.usePattern[0].value() == 'false'
        typeConfigNode.pattern[0].value() == ''
        (1.._) * jobManagement.requirePlugin('violations')
    }

    def 'call violations plugin with all args'() {
        when:
        context.violations(50) {
            sourcePathPattern 'source pattern'
            fauxProjectPath 'faux path'
            perFileDisplayLimit 51
            checkstyle(10, 11, 10, 'test-report/*.xml')
            jshint(0, 0, 0, 'test-report/*.xml')
            findbugs(12, 13, 12)
        }

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.violations.ViolationsPublisher'
        publisherNode.config[0].suppressions.size() == 1
        publisherNode.config[0].limit[0].value() == '51'
        publisherNode.config[0].sourcePathPattern[0].value() == 'source pattern'
        publisherNode.config[0].fauxProjectPath[0].value() == 'faux path'
        publisherNode.config[0].encoding[0].value() == 'default'
        def typeConfigsNode = publisherNode.config[0].typeConfigs[0]
        typeConfigsNode.entry.size() == 17
        def checkstyleNode = typeConfigsNode.entry.find { it.string[0].value() == 'checkstyle' }
        checkstyleNode != null
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].type[0].value() == 'checkstyle'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].min[0].value() == '10'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].max[0].value() == '11'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].unstable[0].value() == '10'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].usePattern[0].value() == 'true'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].pattern[0].value() == 'test-report/*.xml'
        def jshintNode = typeConfigsNode.entry.find { it.string[0].value() == 'jshint' }
        jshintNode != null
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].type[0].value() == 'jshint'
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].min[0].value() == '0'
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].max[0].value() == '0'
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].unstable[0].value() == '0'
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].usePattern[0].value() == 'true'
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].pattern[0].value() == 'test-report/*.xml'
        def findbugsNode = typeConfigsNode.entry.find { it.string[0].value() == 'findbugs' }
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].type[0].value() == 'findbugs'
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].min[0].value() == '12'
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].max[0].value() == '13'
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].unstable[0].value() == '12'
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].usePattern[0].value() == 'false'
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].pattern[0].value() == ''
        def jslintNode = typeConfigsNode.entry.find { it.string[0].value() == 'jslint' }
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].type[0].value() == 'jslint'
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].min[0].value() == '10'
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].max[0].value() == '999'
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].unstable[0].value() == '999'
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].usePattern[0].value() == 'false'
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].pattern[0].value() == ''
        1 * jobManagement.requirePlugin('violations')
    }

    def 'cordell walker constructs xml'() {
        when:
        context.chucknorris()

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.chucknorris.CordellWalkerRecorder'
        publisherNode.value()[0].name() == 'factGenerator'
        publisherNode.value()[0].value() == ''
        1 * jobManagement.requirePlugin('chucknorris')
    }

    def 'irc channels are added'() {
        when:
        context.irc {
            channel('#c1')
            channel('#c2')
        }

        then:
        context.publisherNodes.size() == 1
        Node ircPublisher = context.publisherNodes[0]
        ircPublisher.name() == 'hudson.plugins.ircbot.IrcPublisher'
        def targets = ircPublisher.value()[0]
        targets.value().size == 2
        targets.value()[0].name() == 'hudson.plugins.im.GroupChatIMMessageTarget'
        targets.value()[0].value()[0].name() == 'name'
        targets.value()[0].value()[0].value() == '#c1'
        targets.value()[1].name() == 'hudson.plugins.im.GroupChatIMMessageTarget'
        targets.value()[1].value()[0].name() == 'name'
        targets.value()[1].value()[0].value() == '#c2'
        1 * jobManagement.requireMinimumPluginVersion('ircbot', '2.27')
    }

    def 'irc notification strategy is set'() {
        when:
        context.irc {
            strategy('STATECHANGE_ONLY')
        }

        then:
        context.publisherNodes[0].strategy[0].value() == 'STATECHANGE_ONLY'
        1 * jobManagement.requireMinimumPluginVersion('ircbot', '2.27')
    }

    def 'irc notification strategy is set to NEW_FAILURE_AND_FIXED'() {
        when:
        context.irc {
            strategy('NEW_FAILURE_AND_FIXED')
        }

        then:
        context.publisherNodes[0].strategy[0].value() == 'NEW_FAILURE_AND_FIXED'
        1 * jobManagement.requireMinimumPluginVersion('ircbot', '2.27')
        1 * jobManagement.requireMinimumPluginVersion('instant-messaging', '1.26')
    }

    def 'irc notification invalid strategy triggers exception'() {
        when:
        context.irc {
            strategy('invalid')
        }

        then:
        thrown(DslScriptException)
    }

    def 'notifyScmFixers is set'() {
        when:
        context.irc {
            notifyScmFixers(true)
        }

        then:
        context.publisherNodes[0].notifyFixers[0].value() == true
        1 * jobManagement.requireMinimumPluginVersion('ircbot', '2.27')
    }

    def 'irc notification message is set'() {
        when:
        context.irc {
            channel('#c1')
            notificationMessage('SummaryOnly')
        }

        then:
        context.publisherNodes.size() == 1
        Node ircPublisher = context.publisherNodes[0]
        ircPublisher.name() == 'hudson.plugins.ircbot.IrcPublisher'
        ircPublisher.'buildToChatNotifier'[0].attributes()['class'] ==
                'hudson.plugins.im.build_notify.SummaryOnlyBuildToChatNotifier'
        1 * jobManagement.requireMinimumPluginVersion('ircbot', '2.27')
    }

    def 'default notification message is set if not specified'() {
        when:
        context.irc {
            channel('#c1')
        }

        then:
        context.publisherNodes.size() == 1
        Node ircPublisher = context.publisherNodes[0]
        ircPublisher.name() == 'hudson.plugins.ircbot.IrcPublisher'
        ircPublisher.'buildToChatNotifier'[0].attributes()['class'] ==
                'hudson.plugins.im.build_notify.DefaultBuildToChatNotifier'
        1 * jobManagement.requireMinimumPluginVersion('ircbot', '2.27')
    }

    def 'default notification strategy is set if not specified'() {
        when:
        context.irc {
            channel('#c1')
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].strategy[0].value() == 'ALL'
        1 * jobManagement.requireMinimumPluginVersion('ircbot', '2.27')
    }

    def 'given the required cobertura report file name all defaults are set for your pleasure'() {
        when:
        context.cobertura('reportfilename')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].children().size() == 12
        context.publisherNodes[0].coberturaReportFile[0].value() == 'reportfilename'
        context.publisherNodes[0].onlyStable[0].value() == false
        context.publisherNodes[0].failUnhealthy[0].value() == false
        context.publisherNodes[0].failUnstable[0].value() == false
        context.publisherNodes[0].autoUpdateHealth[0].value() == false
        context.publisherNodes[0].autoUpdateStability[0].value() == false
        context.publisherNodes[0].zoomCoverageChart[0].value() == false
        context.publisherNodes[0].failNoReports[0].value() == true
        assertTarget('healthyTarget', 0, 'METHOD', '8000000')
        assertTarget('healthyTarget', 1, 'LINE', '8000000')
        assertTarget('healthyTarget', 2, 'CONDITIONAL', '7000000')
        assertTarget('unhealthyTarget', 0, 'METHOD', '0')
        assertTarget('unhealthyTarget', 1, 'LINE', '0')
        assertTarget('unhealthyTarget', 2, 'CONDITIONAL', '0')
        assertTarget('failingTarget', 0, 'METHOD', '0')
        assertTarget('failingTarget', 1, 'LINE', '0')
        assertTarget('failingTarget', 2, 'CONDITIONAL', '0')
        context.publisherNodes[0].sourceEncoding[0].value() == 'ASCII'
        1 * jobManagement.requirePlugin('cobertura')
    }

    private void assertTarget(String targetName, int position, String type, String value) {
        def entry = context.publisherNodes[0]."${targetName}"[0].targets[0].entry[position]
        assert entry.'hudson.plugins.cobertura.targets.CoverageMetric'[0].value() == type
        assert entry.'int'[0].value() == value
    }

    def 'the closure makes it possible to override all the cobertura flags'() {
        when:
        context.cobertura('reportfilename') {
            onlyStable(true)
            failUnhealthy(true)
            failUnstable(true)
            autoUpdateHealth(true)
            autoUpdateStability(true)
            zoomCoverageChart(true)
            failNoReports(false)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].children().size() == 12
        context.publisherNodes[0].coberturaReportFile[0].value() == 'reportfilename'
        context.publisherNodes[0].onlyStable[0].value() == true
        context.publisherNodes[0].failUnhealthy[0].value() == true
        context.publisherNodes[0].failUnstable[0].value() == true
        context.publisherNodes[0].autoUpdateHealth[0].value() == true
        context.publisherNodes[0].autoUpdateStability[0].value() == true
        context.publisherNodes[0].zoomCoverageChart[0].value() == true
        context.publisherNodes[0].failNoReports[0].value() == false
        1 * jobManagement.requirePlugin('cobertura')
    }

    def 'overriding cobertura default targets'() {
        when:
        context.cobertura('reportfilename') {
            methodTarget(1, 2, 3)
            lineTarget(4, 5, 6)
            conditionalTarget(7, 8, 9)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].children().size() == 12
        assertTarget('healthyTarget', 0, 'METHOD', '100000')
        assertTarget('unhealthyTarget', 0, 'METHOD', '200000')
        assertTarget('failingTarget', 0, 'METHOD', '300000')
        assertTarget('healthyTarget', 1, 'LINE', '400000')
        assertTarget('unhealthyTarget', 1, 'LINE', '500000')
        assertTarget('failingTarget', 1, 'LINE', '600000')
        assertTarget('healthyTarget', 2, 'CONDITIONAL', '700000')
        assertTarget('unhealthyTarget', 2, 'CONDITIONAL', '800000')
        assertTarget('failingTarget', 2, 'CONDITIONAL', '900000')
        1 * jobManagement.requirePlugin('cobertura')
    }

    def 'adding cobertura extra targets'() {
        when:
        context.cobertura('reportfilename') {
            fileTarget(1, 2, 3)
            packageTarget(4, 5, 6)
            classTarget(7, 8, 9)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].children().size() == 12
        assertTarget('healthyTarget', 3, 'FILES', '100000')
        assertTarget('unhealthyTarget', 3, 'FILES', '200000')
        assertTarget('failingTarget', 3, 'FILES', '300000')
        assertTarget('healthyTarget', 4, 'PACKAGES', '400000')
        assertTarget('unhealthyTarget', 4, 'PACKAGES', '500000')
        assertTarget('failingTarget', 4, 'PACKAGES', '600000')
        assertTarget('healthyTarget', 5, 'CLASSES', '700000')
        assertTarget('unhealthyTarget', 5, 'CLASSES', '800000')
        assertTarget('failingTarget', 5, 'CLASSES', '900000')
        1 * jobManagement.requirePlugin('cobertura')
    }

    def 'checking for invalid cobertura target type'() {
        when:
        context.cobertura('reportfilename') {
            target('invalid', 1, 2, 3)
        }

        then:
        thrown(DslScriptException)
    }

    def 'checking for invalid cobertura target treshold: negative'() {
        when:
        context.cobertura('reportfilename') {
            target('invalid', h, u, f)
        }

        then:
        thrown(DslScriptException)

        where:
        h  | u  | f
        -1 | 1  | 1
        1  | -1 | 1
        1  | 1  | -1
    }

    def 'checking for invalid cobertura target treshold: more than 100 percent'() {
        when:
        context.cobertura('reportfilename') {
            target('invalid', h, u, f)
        }

        then:
        thrown(DslScriptException)

        where:
        h   | u   | f
        101 | 1   | 1
        1   | 101 | 1
        1   | 1   | 101
    }

    def 'null source encoding for cobertura'() {
        when:
        context.cobertura('reportfilename') {
            sourceEncoding(null)
        }
        then:
        thrown(DslScriptException)
    }

    def 'UTF-8 source encoding for cobertura should be the default instead of ASCII'() {
        when:
        context.cobertura('reportfilename') {
            sourceEncoding('UTF-8')
        }
        then:
        context.publisherNodes[0].sourceEncoding[0].value() == 'UTF-8'
        1 * jobManagement.requirePlugin('cobertura')
    }

    def 'call allowBrokenBuildClaiming'() {
        when:
        context.allowBrokenBuildClaiming()

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.claim.ClaimPublisher'
        1 * jobManagement.requirePlugin('claim')
    }

    def 'add fingerprinting'(targets, recordArtifacts) {
        when:
        context.fingerprint(targets, recordArtifacts)

        then:
        context.publisherNodes.size() == 1
        def fingerprintNode = context.publisherNodes[0]
        fingerprintNode.targets[0].value() == targets
        fingerprintNode.recordBuildArtifacts[0].value() == recordArtifacts

        where:
        targets    | recordArtifacts
        '**/*'     | false
        ''         | true
        '**/*arst' | false
        'whatever' | true
    }

    def 'call buildDescription with one argument'() {
        when:
        context.buildDescription('success')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.descriptionsetter.DescriptionSetterPublisher'
        context.publisherNodes[0].children().size() == 3
        context.publisherNodes[0].regexp[0].value() == 'success'
        context.publisherNodes[0].regexpForFailed[0].value() == ''
        context.publisherNodes[0].setForMatrix[0].value() == false
        1 * jobManagement.requirePlugin('description-setter')
    }

    def 'call buildDescription with two arguments'() {
        when:
        context.buildDescription('success', 'AWSUM!')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.descriptionsetter.DescriptionSetterPublisher'
        context.publisherNodes[0].children().size() == 4
        context.publisherNodes[0].regexp[0].value() == 'success'
        context.publisherNodes[0].regexpForFailed[0].value() == ''
        context.publisherNodes[0].description[0].value() == 'AWSUM!'
        context.publisherNodes[0].setForMatrix[0].value() == false
        1 * jobManagement.requirePlugin('description-setter')
    }

    def 'call buildDescription with three arguments'() {
        when:
        context.buildDescription('success', 'AWSUM!', 'failed')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.descriptionsetter.DescriptionSetterPublisher'
        context.publisherNodes[0].children().size() == 4
        context.publisherNodes[0].regexp[0].value() == 'success'
        context.publisherNodes[0].regexpForFailed[0].value() == 'failed'
        context.publisherNodes[0].description[0].value() == 'AWSUM!'
        context.publisherNodes[0].setForMatrix[0].value() == false
        1 * jobManagement.requirePlugin('description-setter')
    }

    def 'call buildDescription with four arguments'() {
        when:
        context.buildDescription('success', 'AWSUM!', 'failed', 'NOES!')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.descriptionsetter.DescriptionSetterPublisher'
        context.publisherNodes[0].children().size() == 5
        context.publisherNodes[0].regexp[0].value() == 'success'
        context.publisherNodes[0].regexpForFailed[0].value() == 'failed'
        context.publisherNodes[0].description[0].value() == 'AWSUM!'
        context.publisherNodes[0].descriptionForFailed[0].value() == 'NOES!'
        context.publisherNodes[0].setForMatrix[0].value() == false
        1 * jobManagement.requirePlugin('description-setter')
    }

    def 'call buildDescription with five arguments'() {
        when:
        context.buildDescription('success', 'AWSUM!', 'failed', 'NOES!', true)

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.descriptionsetter.DescriptionSetterPublisher'
        context.publisherNodes[0].children().size() == 5
        context.publisherNodes[0].regexp[0].value() == 'success'
        context.publisherNodes[0].regexpForFailed[0].value() == 'failed'
        context.publisherNodes[0].description[0].value() == 'AWSUM!'
        context.publisherNodes[0].descriptionForFailed[0].value() == 'NOES!'
        context.publisherNodes[0].setForMatrix[0].value() == true
        1 * jobManagement.requirePlugin('description-setter')
    }

    def 'call textFinder with one argument'() {
        when:
        context.textFinder('foo')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.textfinder.TextFinderPublisher'
        context.publisherNodes[0].regexp[0].value() == 'foo'
        context.publisherNodes[0].fileSet.size() == 0
        context.publisherNodes[0].alsoCheckConsoleOutput[0].value() == false
        context.publisherNodes[0].succeedIfFound[0].value() == false
        context.publisherNodes[0].unstableIfFound[0].value() == false
        1 * jobManagement.requirePlugin('text-finder')
    }

    def 'call textFinder with two arguments'() {
        when:
        context.textFinder('foo', '*.txt')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.textfinder.TextFinderPublisher'
        context.publisherNodes[0].regexp[0].value() == 'foo'
        context.publisherNodes[0].fileSet[0].value() == '*.txt'
        context.publisherNodes[0].alsoCheckConsoleOutput[0].value() == false
        context.publisherNodes[0].succeedIfFound[0].value() == false
        context.publisherNodes[0].unstableIfFound[0].value() == false
        1 * jobManagement.requirePlugin('text-finder')
    }

    def 'call textFinder with three arguments'() {
        when:
        context.textFinder('foo', '*.txt', true)

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.textfinder.TextFinderPublisher'
        context.publisherNodes[0].regexp[0].value() == 'foo'
        context.publisherNodes[0].fileSet[0].value() == '*.txt'
        context.publisherNodes[0].alsoCheckConsoleOutput[0].value() == true
        context.publisherNodes[0].succeedIfFound[0].value() == false
        context.publisherNodes[0].unstableIfFound[0].value() == false
        1 * jobManagement.requirePlugin('text-finder')
    }

    def 'call textFinder with four arguments'() {
        when:
        context.textFinder('foo', '*.txt', true, true)

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.textfinder.TextFinderPublisher'
        context.publisherNodes[0].regexp[0].value() == 'foo'
        context.publisherNodes[0].fileSet[0].value() == '*.txt'
        context.publisherNodes[0].alsoCheckConsoleOutput[0].value() == true
        context.publisherNodes[0].succeedIfFound[0].value() == true
        context.publisherNodes[0].unstableIfFound[0].value() == false
        1 * jobManagement.requirePlugin('text-finder')
    }

    def 'call textFinder with five arguments'() {
        when:
        context.textFinder('foo', '*.txt', true, true, true)

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.textfinder.TextFinderPublisher'
        context.publisherNodes[0].regexp[0].value() == 'foo'
        context.publisherNodes[0].fileSet[0].value() == '*.txt'
        context.publisherNodes[0].alsoCheckConsoleOutput[0].value() == true
        context.publisherNodes[0].succeedIfFound[0].value() == true
        context.publisherNodes[0].unstableIfFound[0].value() == true
        1 * jobManagement.requirePlugin('text-finder')
    }

    def 'call postBuildTask with two arguments'() {
        when:
        context.postBuildTask {
            task('BUILD SUCCESSFUL', 'git clean -fdx')
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.postbuildtask.PostbuildTask'
        with(context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0]) {
            logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].logText[0].value() == 'BUILD SUCCESSFUL'
            logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].operator[0].value() == 'AND'
            EscalateStatus[0].value() == false
            RunIfJobSuccessful[0].value() == false
            script[0].value() == 'git clean -fdx'
        }
        1 * jobManagement.requirePlugin('postbuild-task')
    }

    def 'call postBuildTask with two tasks'() {
        when:
        context.postBuildTask {
            task('BUILD SUCCESSFUL', 'git clean -fdx')
            task('BUILD FAILED', 'git gc', true, true)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.postbuildtask.PostbuildTask'
        with(context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0]) {
            logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].logText[0].value() == 'BUILD SUCCESSFUL'
            logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].operator[0].value() == 'AND'
            EscalateStatus[0].value() == false
            RunIfJobSuccessful[0].value() == false
            script[0].value() == 'git clean -fdx'
        }
        with(context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[1]) {
            logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].logText[0].value() == 'BUILD FAILED'
            logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].operator[0].value() == 'AND'
            EscalateStatus[0].value() == true
            RunIfJobSuccessful[0].value() == true
            script[0].value() == 'git gc'
        }
        1 * jobManagement.requirePlugin('postbuild-task')
    }

    def 'call aggregate downstream test results with no args'() {
        when:
        context.aggregateDownstreamTestResults()

        then:
        Node aggregateNode = context.publisherNodes[0]
        aggregateNode.name() == 'hudson.tasks.test.AggregatedTestResultPublisher'
        aggregateNode.jobs[0] == null
        aggregateNode.includeFailedBuilds[0].value() == false
    }

    def 'call aggregate downstream test results with job listing'() {
        when:
        context.aggregateDownstreamTestResults('project-A, project-B')

        then:
        Node aggregateNode = context.publisherNodes[0]
        aggregateNode.name() == 'hudson.tasks.test.AggregatedTestResultPublisher'
        aggregateNode.jobs[0].value() == 'project-A, project-B'
        aggregateNode.includeFailedBuilds[0].value() == false
    }

    def 'call aggregate downstream test results with null job listing and overriden includeFailedBuilds'() {
        when:
        context.aggregateDownstreamTestResults(null, true)

        then:
        Node aggregateNode = context.publisherNodes[0]
        aggregateNode.name() == 'hudson.tasks.test.AggregatedTestResultPublisher'
        aggregateNode.jobs[0] == null
        aggregateNode.includeFailedBuilds[0].value() == true
    }

    def 'call aggregate downstream test results with job listing and overriden includeFailedBuilds'() {
        when:
        context.aggregateDownstreamTestResults('project-A, project-B', true)

        then:
        Node aggregateNode = context.publisherNodes[0]
        aggregateNode.name() == 'hudson.tasks.test.AggregatedTestResultPublisher'
        aggregateNode.jobs[0].value() == 'project-A, project-B'
        aggregateNode.includeFailedBuilds[0].value() == true
    }

    def 'call groovyPostBuild'() {
        when:
        context.groovyPostBuild('foo')

        then:
        with(context.publisherNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder'
            children().size() == 2
            with(script[0]) {
                children().size() == 2
                script[0].value() == 'foo'
                sandbox[0].value() == false
            }
            behavior[0].value() == 0
        }
        (1.._) * jobManagement.requireMinimumPluginVersion('groovy-postbuild', '2.2')
    }

    def 'call groovyPostBuild with overridden failure behavior'() {
        when:
        context.groovyPostBuild('foo', MarkUnstable)

        then:
        with(context.publisherNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder'
            children().size() == 2
            with(script[0]) {
                children().size() == 2
                script[0].value() == 'foo'
                sandbox[0].value() == false
            }
            behavior[0].value() == 1
        }
        (1.._) * jobManagement.requireMinimumPluginVersion('groovy-postbuild', '2.2')
    }

    def 'call groovyPostBuild with closure and no options'() {
        when:
        context.groovyPostBuild {
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder'
            children().size() == 2
            with(script[0]) {
                children().size() == 2
                script[0].value() == ''
                sandbox[0].value() == false
            }
            behavior[0].value() == 0
        }
        1 * jobManagement.requireMinimumPluginVersion('groovy-postbuild', '2.2')
    }

    def 'call groovyPostBuild with closure and all options'() {
        when:
        context.groovyPostBuild {
            script('foo')
            behavior(PublisherContext.Behavior.MarkFailed)
            sandbox()
            classpath('foo', 'bar')
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder'
            children().size() == 2
            with(script[0]) {
                children().size() == 3
                script[0].value() == 'foo'
                sandbox[0].value() == true
                with(classpath[0]) {
                    children().size() == 2
                    with(entry[0]) {
                        children().size() == 1
                        url[0].value() =~ 'file:.*/foo'
                    }
                    with(entry[1]) {
                        children().size() == 1
                        url[0].value() =~ 'file:.*/bar'
                    }
                }
            }
            behavior[0].value() == 2
        }
        1 * jobManagement.requireMinimumPluginVersion('groovy-postbuild', '2.2')
    }

    def 'call javadoc archiver with no args'() {
        when:
        context.archiveJavadoc()

        then:
        Node javadocNode = context.publisherNodes[0]
        javadocNode.name() == 'hudson.tasks.JavadocArchiver'
        javadocNode.javadocDir[0].value() == ''
        javadocNode.keepAll[0].value() == false
        1 * jobManagement.requirePlugin('javadoc')
    }

    def 'call javadoc archiver with all args'() {
        when:
        context.archiveJavadoc {
            javadocDir 'build/javadoc'
            keepAll true
        }

        then:
        Node javadocNode = context.publisherNodes[0]
        javadocNode.name() == 'hudson.tasks.JavadocArchiver'
        javadocNode.javadocDir[0].value() == 'build/javadoc'
        javadocNode.keepAll[0].value() == true
        1 * jobManagement.requirePlugin('javadoc')
    }

    def 'call associated files with normal args'() {
        when:
        context.associatedFiles('/foo/file/${VARIABLE}')

        then:
        Node associatedFilesNode = context.publisherNodes[0]
        associatedFilesNode.name() == 'org.jenkinsci.plugins.associatedfiles.AssociatedFilesPublisher'
        associatedFilesNode.associatedFiles[0].value() == '/foo/file/${VARIABLE}'
        1 * jobManagement.requirePlugin('associated-files')
    }

    def 'call emma with one argument'() {
        when:
        context.emma('coverage-results/coverage.xml')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.emma.EmmaPublisher'
        context.publisherNodes[0].includes[0].value() == 'coverage-results/coverage.xml'
        context.publisherNodes[0].healthReports[0].minClass[0].value() == 0
        context.publisherNodes[0].healthReports[0].maxClass[0].value() == 100
        context.publisherNodes[0].healthReports[0].minMethod[0].value() == 0
        context.publisherNodes[0].healthReports[0].maxMethod[0].value() == 70
        context.publisherNodes[0].healthReports[0].minBlock[0].value() == 0
        context.publisherNodes[0].healthReports[0].maxBlock[0].value() == 80
        context.publisherNodes[0].healthReports[0].minLine[0].value() == 0
        context.publisherNodes[0].healthReports[0].maxLine[0].value() == 80
        context.publisherNodes[0].healthReports[0].minCondition[0].value() == 0
        context.publisherNodes[0].healthReports[0].maxCondition[0].value() == 80
        1 * jobManagement.requirePlugin('emma')
    }

    def 'call emma with range thresholds'() {
        when:
        context.emma('coverage-results/coverage.xml') {
            classThreshold(5..90)
            methodThreshold(10..80)
            blockThreshold(15..75)
            lineThreshold(20..70)
            conditionThreshold(25..65)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.emma.EmmaPublisher'
        context.publisherNodes[0].includes[0].value() == 'coverage-results/coverage.xml'
        context.publisherNodes[0].healthReports[0].minClass[0].value() == 5
        context.publisherNodes[0].healthReports[0].maxClass[0].value() == 90
        context.publisherNodes[0].healthReports[0].minMethod[0].value() == 10
        context.publisherNodes[0].healthReports[0].maxMethod[0].value() == 80
        context.publisherNodes[0].healthReports[0].minBlock[0].value() == 15
        context.publisherNodes[0].healthReports[0].maxBlock[0].value() == 75
        context.publisherNodes[0].healthReports[0].minLine[0].value() == 20
        context.publisherNodes[0].healthReports[0].maxLine[0].value() == 70
        context.publisherNodes[0].healthReports[0].minCondition[0].value() == 25
        context.publisherNodes[0].healthReports[0].maxCondition[0].value() == 65
        1 * jobManagement.requirePlugin('emma')
    }

    def 'call emma with individual thresholds'() {
        when:
        context.emma('coverage-results/coverage.xml') {
            minClass(5)
            maxClass(90)
            minMethod(10)
            maxMethod(80)
            minBlock(15)
            maxBlock(75)
            minLine(20)
            maxLine(70)
            minCondition(25)
            maxCondition(65)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.emma.EmmaPublisher'
        context.publisherNodes[0].includes[0].value() == 'coverage-results/coverage.xml'
        context.publisherNodes[0].healthReports[0].minClass[0].value() == 5
        context.publisherNodes[0].healthReports[0].maxClass[0].value() == 90
        context.publisherNodes[0].healthReports[0].minMethod[0].value() == 10
        context.publisherNodes[0].healthReports[0].maxMethod[0].value() == 80
        context.publisherNodes[0].healthReports[0].minBlock[0].value() == 15
        context.publisherNodes[0].healthReports[0].maxBlock[0].value() == 75
        context.publisherNodes[0].healthReports[0].minLine[0].value() == 20
        context.publisherNodes[0].healthReports[0].maxLine[0].value() == 70
        context.publisherNodes[0].healthReports[0].minCondition[0].value() == 25
        context.publisherNodes[0].healthReports[0].maxCondition[0].value() == 65
        1 * jobManagement.requirePlugin('emma')
    }

    def 'call emma with bad range values'() {
        when:
        context.emma('coverage-results/coverage.xml') {
            minClass(-5)
        }

        then:
        thrown(DslScriptException)

        when:
        context.emma('coverage-results/coverage.xml') {
            minLine(101)
        }

        then:
        thrown(DslScriptException)

        when:
        context.emma('coverage-results/coverage.xml') {
            maxCondition(101)
        }

        then:
        thrown(DslScriptException)

        when:
        context.emma('coverage-results/coverage.xml') {
            maxBlock(-1)
        }

        then:
        thrown(DslScriptException)

        when:
        context.emma('coverage-results/coverage.xml') {
            classThreshold(-5..90)
        }

        then:
        thrown(DslScriptException)

        when:
        context.emma('coverage-results/coverage.xml') {
            methodThreshold(5..101)
        }

        then:
        thrown(DslScriptException)
    }

    def 'publish Robot framework report using default values'() {
        when:
        context.publishRobotFrameworkReports()

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.robot.RobotPublisher'
            children().size() == 9
            outputPath[0].value() == 'target/robotframework-reports'
            passThreshold[0].value() == 100.0
            unstableThreshold[0].value() == 0.0
            onlyCritical[0].value() == false
            reportFileName[0].value() == 'report.html'
            logFileName[0].value() == 'log.html'
            outputFileName[0].value() == 'output.xml'
            disableArchiveOutput[0].value() == false
            otherFiles[0].value().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('robot', '1.4.3')
    }

    def 'publish Robot framework report using all options'() {
        when:
        context.publishRobotFrameworkReports {
            outputPath('/path/to/foo')
            passThreshold(90.0)
            unstableThreshold(10.0)
            onlyCritical()
            reportFileName('project.html')
            logFileName('out.html')
            outputFileName('out.xml')
            disableArchiveOutput()
            otherFiles('screenshot-1.png', 'screenshot-2.png')
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.robot.RobotPublisher'
            children().size() == 9
            outputPath[0].value() == '/path/to/foo'
            passThreshold[0].value() == 90.0
            unstableThreshold[0].value() == 10.0
            onlyCritical[0].value() == true
            reportFileName[0].value() == 'project.html'
            logFileName[0].value() == 'out.html'
            outputFileName[0].value() == 'out.xml'
            disableArchiveOutput[0].value() == true
            otherFiles[0].string[0].value() == 'screenshot-1.png'
            otherFiles[0].string[1].value() == 'screenshot-2.png'
        }
        1 * jobManagement.requireMinimumPluginVersion('robot', '1.4.3')
    }

    def 'call buildPipelineTrigger'() {
        when:
        context.buildPipelineTrigger('next')

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'au.com.centrumsystems.hudson.plugin.buildpipeline.trigger.BuildPipelineTrigger'
            downstreamProjectNames.size() == 1
            downstreamProjectNames[0].value() == 'next'
            configs.size() == 1
            configs[0].value().empty
        }
        1 * jobManagement.requirePlugin('build-pipeline-plugin')
    }

    def 'call buildPipelineTrigger with empty parameters'() {
        when:
        context.buildPipelineTrigger('next') {
            parameters {
            }
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'au.com.centrumsystems.hudson.plugin.buildpipeline.trigger.BuildPipelineTrigger'
            downstreamProjectNames.size() == 1
            downstreamProjectNames[0].value() == 'next'
            configs.size() == 1
            configs[0].value().empty
        }
        1 * jobManagement.requirePlugin('build-pipeline-plugin')
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
    }

    def 'call buildPipelineTrigger with parameters'() {
        when:
        context.buildPipelineTrigger('next') {
            parameters {
                currentBuild()
                predefinedProp('key1', 'value1')
            }
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'au.com.centrumsystems.hudson.plugin.buildpipeline.trigger.BuildPipelineTrigger'

            downstreamProjectNames.size() == 1
            downstreamProjectNames[0].value() == 'next'

            configs.size() == 1
            configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'.size() == 1
            configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'[0].value().empty
            configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'.size() == 1
            configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'[0].'properties'[0].value() ==
                    'key1=value1'
        }
        1 * jobManagement.requirePlugin('build-pipeline-plugin')
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
    }

    def 'call buildPipelineTrigger with null argument'() {
        when:
        context.buildPipelineTrigger(null)

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'au.com.centrumsystems.hudson.plugin.buildpipeline.trigger.BuildPipelineTrigger'
            downstreamProjectNames.size() == 1
            downstreamProjectNames[0].value() == ''
            configs.size() == 1
            configs[0].value().empty
        }
        1 * jobManagement.requirePlugin('build-pipeline-plugin')
    }

    def 'call github commit notifier methods'() {
        when:
        context.githubCommitNotifier()

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        def githubCommitNotifier = context.publisherNodes[0]
        githubCommitNotifier.name() == 'com.cloudbees.jenkins.GitHubCommitNotifier'
        1 * jobManagement.requirePlugin('github')
    }

    def 'call git with minimal options'() {
        when:
        context.git {
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.git.GitPublisher'
        context.publisherNodes[0].children().size() == 6
        context.publisherNodes[0].configVersion[0].value() == 2
        context.publisherNodes[0].pushMerge[0].value() == false
        context.publisherNodes[0].pushOnlyIfSuccess[0].value() == false
        context.publisherNodes[0].forcePush[0].value() == false
        1 * jobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git with all options'() {
        when:
        context.git {
            pushOnlyIfSuccess()
            pushMerge()
            forcePush()
            tag('origin', 'test') {
                message('test tag')
                create()
                update()
            }
            branch('origin', 'master')
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.git.GitPublisher'
            configVersion[0].value() == 2
            pushMerge[0].value() == true
            pushOnlyIfSuccess[0].value() == true
            forcePush[0].value() == true
            tagsToPush.size() == 1
            tagsToPush[0].'hudson.plugins.git.GitPublisher_-TagToPush'.size() == 1
            with(tagsToPush[0].'hudson.plugins.git.GitPublisher_-TagToPush'[0]) {
                targetRepoName[0].value() == 'origin'
                tagName[0].value() == 'test'
                tagMessage[0].value() == 'test tag'
                createTag[0].value() == true
                updateTag[0].value() == true
            }
            branchesToPush.size() == 1
            branchesToPush[0].'hudson.plugins.git.GitPublisher_-BranchToPush'.size() == 1
            with(branchesToPush[0].'hudson.plugins.git.GitPublisher_-BranchToPush'[0]) {
                targetRepoName[0].value() == 'origin'
                branchName[0].value() == 'master'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git with minimal tag options'() {
        when:
        context.git {
            tag('origin', 'test')
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.git.GitPublisher'
            configVersion[0].value() == 2
            pushMerge[0].value() == false
            pushOnlyIfSuccess[0].value() == false
            forcePush[0].value() == false
            tagsToPush.size() == 1
            tagsToPush[0].'hudson.plugins.git.GitPublisher_-TagToPush'.size() == 1
            with(tagsToPush[0].'hudson.plugins.git.GitPublisher_-TagToPush'[0]) {
                targetRepoName[0].value() == 'origin'
                tagName[0].value() == 'test'
                tagMessage[0].value() == ''
                createTag[0].value() == false
                updateTag[0].value() == false
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git without tag targetRepoName'() {
        when:
        context.git {
            tag(null, 'test')
        }

        then:
        thrown(DslScriptException)

        when:
        context.git {
            tag('', 'test')
        }

        then:
        thrown(DslScriptException)
    }

    def 'call git without tag name'() {
        when:
        context.git {
            tag('origin', null)
        }

        then:
        thrown(DslScriptException)

        when:
        context.git {
            tag('origin', '')
        }

        then:
        thrown(DslScriptException)
    }

    def 'call git without branch targetRepoName'() {
        when:
        context.git {
            branch(null, 'test')
        }

        then:
        thrown(DslScriptException)

        when:
        context.git {
            branch('', 'test')
        }

        then:
        thrown(DslScriptException)
    }

    def 'call git without branch name'() {
        when:
        context.git {
            branch('origin', null)
        }

        then:
        thrown(DslScriptException)

        when:
        context.git {
            branch('origin', '')
        }

        then:
        thrown(DslScriptException)
    }

    def 'flowdock with default notification settings'() {
        when:
        context.flowdock('some-madeup-token')

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'com.flowdock.jenkins.FlowdockNotifier'
            flowToken[0].value() == 'some-madeup-token'
            chatNotification[0].value() == false
            notificationTags[0].value() == ''
            notifySuccess[0].value() == true
            notifyFailure[0].value() == true
            notifyFixed[0].value() == true
            notifyUnstable[0].value() == false
            notifyAborted[0].value() == false
            notifyNotBuilt[0].value() == false
            notifyMap.size() == 1
            notifyMap[0].entry.size() == 6
            with(notifyMap[0]) {
                entry[0].'com.flowdock.jenkins.BuildResult'[0].value() == 'ABORTED'
                entry[0].boolean[0].value() == false
                entry[1].'com.flowdock.jenkins.BuildResult'[0].value() == 'SUCCESS'
                entry[1].boolean[0].value() == true
                entry[2].'com.flowdock.jenkins.BuildResult'[0].value() == 'FIXED'
                entry[2].boolean[0].value() == true
                entry[3].'com.flowdock.jenkins.BuildResult'[0].value() == 'UNSTABLE'
                entry[3].boolean[0].value() == false
                entry[4].'com.flowdock.jenkins.BuildResult'[0].value() == 'FAILURE'
                entry[4].boolean[0].value() == true
                entry[5].'com.flowdock.jenkins.BuildResult'[0].value() == 'NOT_BUILT'
                entry[5].boolean[0].value() == false
            }
        }
        1 * jobManagement.requirePlugin('jenkins-flowdock-plugin')
    }

    def 'flowdock with some overridden notification settings'() {
        when:
        context.flowdock('another-token') {
            unstable()
            success(false)
            chat()
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'com.flowdock.jenkins.FlowdockNotifier'
            flowToken[0].value() == 'another-token'
            chatNotification[0].value() == true
            notificationTags[0].value() == ''
            notifySuccess[0].value() == false
            notifyFailure[0].value() == true
            notifyFixed[0].value() == true
            notifyUnstable[0].value() == true
            notifyAborted[0].value() == false
            notifyNotBuilt[0].value() == false
            notifyMap.size() == 1
            notifyMap[0].entry.size() == 6
            with(notifyMap[0]) {
                entry[0].'com.flowdock.jenkins.BuildResult'[0].value() == 'ABORTED'
                entry[0].boolean[0].value() == false
                entry[1].'com.flowdock.jenkins.BuildResult'[0].value() == 'SUCCESS'
                entry[1].boolean[0].value() == false
                entry[2].'com.flowdock.jenkins.BuildResult'[0].value() == 'FIXED'
                entry[2].boolean[0].value() == true
                entry[3].'com.flowdock.jenkins.BuildResult'[0].value() == 'UNSTABLE'
                entry[3].boolean[0].value() == true
                entry[4].'com.flowdock.jenkins.BuildResult'[0].value() == 'FAILURE'
                entry[4].boolean[0].value() == true
                entry[5].'com.flowdock.jenkins.BuildResult'[0].value() == 'NOT_BUILT'
                entry[5].boolean[0].value() == false
            }
        }
        1 * jobManagement.requirePlugin('jenkins-flowdock-plugin')
    }

    def 'flowdock trigger methods with no args defaults their value to true'() {
        when:
        context.flowdock('another-token') {
            unstable()
            success()
            aborted()
            failure()
            fixed()
            notBuilt()
            chat()
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'com.flowdock.jenkins.FlowdockNotifier'
            flowToken[0].value() == 'another-token'
            chatNotification[0].value() == true
            notificationTags[0].value() == ''
            notifySuccess[0].value() == true
            notifyFailure[0].value() == true
            notifyFixed[0].value() == true
            notifyUnstable[0].value() == true
            notifyAborted[0].value() == true
            notifyNotBuilt[0].value() == true
            notifyMap.size() == 1
            notifyMap[0].entry.size() == 6
            with(notifyMap[0]) {
                entry[0].'com.flowdock.jenkins.BuildResult'[0].value() == 'ABORTED'
                entry[0].boolean[0].value() == true
                entry[1].'com.flowdock.jenkins.BuildResult'[0].value() == 'SUCCESS'
                entry[1].boolean[0].value() == true
                entry[2].'com.flowdock.jenkins.BuildResult'[0].value() == 'FIXED'
                entry[2].boolean[0].value() == true
                entry[3].'com.flowdock.jenkins.BuildResult'[0].value() == 'UNSTABLE'
                entry[3].boolean[0].value() == true
                entry[4].'com.flowdock.jenkins.BuildResult'[0].value() == 'FAILURE'
                entry[4].boolean[0].value() == true
                entry[5].'com.flowdock.jenkins.BuildResult'[0].value() == 'NOT_BUILT'
                entry[5].boolean[0].value() == true
            }
        }
        1 * jobManagement.requirePlugin('jenkins-flowdock-plugin')
    }

    def 'flowdock with all non-default args set'() {
        when:
        context.flowdock('another-token') {
            unstable(true)
            success(false)
            aborted(true)
            failure(false)
            fixed(false)
            notBuilt(true)
            chat(true)
            tag('tag1')
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'com.flowdock.jenkins.FlowdockNotifier'
            flowToken[0].value() == 'another-token'
            chatNotification[0].value() == true
            notificationTags[0].value() == 'tag1'
            notifySuccess[0].value() == false
            notifyFailure[0].value() == false
            notifyFixed[0].value() == false
            notifyUnstable[0].value() == true
            notifyAborted[0].value() == true
            notifyNotBuilt[0].value() == true
            notifyMap.size() == 1
            notifyMap[0].entry.size() == 6
            with(notifyMap[0]) {
                entry[0].'com.flowdock.jenkins.BuildResult'[0].value() == 'ABORTED'
                entry[0].boolean[0].value() == true
                entry[1].'com.flowdock.jenkins.BuildResult'[0].value() == 'SUCCESS'
                entry[1].boolean[0].value() == false
                entry[2].'com.flowdock.jenkins.BuildResult'[0].value() == 'FIXED'
                entry[2].boolean[0].value() == false
                entry[3].'com.flowdock.jenkins.BuildResult'[0].value() == 'UNSTABLE'
                entry[3].boolean[0].value() == true
                entry[4].'com.flowdock.jenkins.BuildResult'[0].value() == 'FAILURE'
                entry[4].boolean[0].value() == false
                entry[5].'com.flowdock.jenkins.BuildResult'[0].value() == 'NOT_BUILT'
                entry[5].boolean[0].value() == true
            }
        }
        1 * jobManagement.requirePlugin('jenkins-flowdock-plugin')
    }

    def 'flowdock with tags'() {
        when:
        context.flowdock('another-token') {
            tags('tag1', 'tagTwo')
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'com.flowdock.jenkins.FlowdockNotifier'
            flowToken[0].value() == 'another-token'
            chatNotification[0].value() == false
            notificationTags[0].value() == 'tag1,tagTwo'
            notifySuccess[0].value() == true
            notifyFailure[0].value() == true
            notifyFixed[0].value() == true
            notifyUnstable[0].value() == false
            notifyAborted[0].value() == false
            notifyNotBuilt[0].value() == false
            notifyMap.size() == 1
            notifyMap[0].entry.size() == 6
            with(notifyMap[0]) {
                entry[0].'com.flowdock.jenkins.BuildResult'[0].value() == 'ABORTED'
                entry[0].boolean[0].value() == false
                entry[1].'com.flowdock.jenkins.BuildResult'[0].value() == 'SUCCESS'
                entry[1].boolean[0].value() == true
                entry[2].'com.flowdock.jenkins.BuildResult'[0].value() == 'FIXED'
                entry[2].boolean[0].value() == true
                entry[3].'com.flowdock.jenkins.BuildResult'[0].value() == 'UNSTABLE'
                entry[3].boolean[0].value() == false
                entry[4].'com.flowdock.jenkins.BuildResult'[0].value() == 'FAILURE'
                entry[4].boolean[0].value() == true
                entry[5].'com.flowdock.jenkins.BuildResult'[0].value() == 'NOT_BUILT'
                entry[5].boolean[0].value() == false
            }
        }
        1 * jobManagement.requirePlugin('jenkins-flowdock-plugin')
    }

    def 'flowdock with multiple tag calls'() {
        when:
        context.flowdock('another-token') {
            tag('tag1')
            tag('tagTwo')
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'com.flowdock.jenkins.FlowdockNotifier'
            flowToken[0].value() == 'another-token'
            chatNotification[0].value() == false
            notificationTags[0].value() == 'tag1,tagTwo'
            notifySuccess[0].value() == true
            notifyFailure[0].value() == true
            notifyFixed[0].value() == true
            notifyUnstable[0].value() == false
            notifyAborted[0].value() == false
            notifyNotBuilt[0].value() == false
            notifyMap.size() == 1
            notifyMap[0].entry.size() == 6
            with(notifyMap[0]) {
                entry[0].'com.flowdock.jenkins.BuildResult'[0].value() == 'ABORTED'
                entry[0].boolean[0].value() == false
                entry[1].'com.flowdock.jenkins.BuildResult'[0].value() == 'SUCCESS'
                entry[1].boolean[0].value() == true
                entry[2].'com.flowdock.jenkins.BuildResult'[0].value() == 'FIXED'
                entry[2].boolean[0].value() == true
                entry[3].'com.flowdock.jenkins.BuildResult'[0].value() == 'UNSTABLE'
                entry[3].boolean[0].value() == false
                entry[4].'com.flowdock.jenkins.BuildResult'[0].value() == 'FAILURE'
                entry[4].boolean[0].value() == true
                entry[5].'com.flowdock.jenkins.BuildResult'[0].value() == 'NOT_BUILT'
                entry[5].boolean[0].value() == false
            }
        }
        1 * jobManagement.requirePlugin('jenkins-flowdock-plugin')
    }

    def 'flowdock with empty tag'() {
        when:
        context.flowdock('token') {
            tag('')
        }

        then:
        thrown(DslScriptException)
    }

    def 'flowdock with multiple tokens'() {
        when:
        context.flowdock('some-madeup-token', 'a-second-token')

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'com.flowdock.jenkins.FlowdockNotifier'
            flowToken[0].value() == 'some-madeup-token,a-second-token'
            chatNotification[0].value() == false
            notificationTags[0].value() == ''
            notifySuccess[0].value() == true
            notifyFailure[0].value() == true
            notifyFixed[0].value() == true
            notifyUnstable[0].value() == false
            notifyAborted[0].value() == false
            notifyNotBuilt[0].value() == false
            notifyMap.size() == 1
            notifyMap[0].entry.size() == 6
            with(notifyMap[0]) {
                entry[0].'com.flowdock.jenkins.BuildResult'[0].value() == 'ABORTED'
                entry[0].boolean[0].value() == false
                entry[1].'com.flowdock.jenkins.BuildResult'[0].value() == 'SUCCESS'
                entry[1].boolean[0].value() == true
                entry[2].'com.flowdock.jenkins.BuildResult'[0].value() == 'FIXED'
                entry[2].boolean[0].value() == true
                entry[3].'com.flowdock.jenkins.BuildResult'[0].value() == 'UNSTABLE'
                entry[3].boolean[0].value() == false
                entry[4].'com.flowdock.jenkins.BuildResult'[0].value() == 'FAILURE'
                entry[4].boolean[0].value() == true
                entry[5].'com.flowdock.jenkins.BuildResult'[0].value() == 'NOT_BUILT'
                entry[5].boolean[0].value() == false
            }
        }
        (1.._) * jobManagement.requirePlugin('jenkins-flowdock-plugin')
    }

    def 'flowdock with no tokens'() {
        when:
        context.flowdock((String[]) null)

        then:
        thrown(DslScriptException)
    }

    def 'stashNotifier with default configuration'() {
        when:
        context.stashNotifier {
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.stashNotifier.StashNotifier'
            children().size() == 6
            stashServerBaseUrl[0].value().empty
            stashUserName[0].value().empty
            stashUserPassword[0].value().empty
            ignoreUnverifiedSSLPeer[0].value() == false
            commitSha1[0].value() == ''
            includeBuildNumberInKey[0].value() == false
        }
        1 * jobManagement.requirePlugin('stashNotifier')
        1 * jobManagement.logPluginDeprecationWarning('stashNotifier', '1.11.6')
    }

    def 'stashNotifier with configuration of all parameters'() {
        when:
        context.stashNotifier {
            serverBaseUrl('test')
            commitSha1('sha1')
            keepRepeatedBuilds(true)
            ignoreUnverifiedSSLCertificates(true)
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.stashNotifier.StashNotifier'
            children().size() == 6
            stashServerBaseUrl[0].value() == 'test'
            stashUserName[0].value().empty
            stashUserPassword[0].value().empty
            ignoreUnverifiedSSLPeer[0].value() == true
            commitSha1[0].value() == 'sha1'
            includeBuildNumberInKey[0].value() == true
        }
        1 * jobManagement.requirePlugin('stashNotifier')
        1 * jobManagement.logPluginDeprecationWarning('stashNotifier', '1.11.6')
    }

    def 'stashNotifier with configuration of all parameters using defaults for boolean parameter'() {
        when:
        context.stashNotifier {
            serverBaseUrl('test')
            commitSha1('sha1')
            keepRepeatedBuilds()
            ignoreUnverifiedSSLCertificates()
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.stashNotifier.StashNotifier'
            children().size() == 6
            stashServerBaseUrl[0].value() == 'test'
            stashUserName[0].value().empty
            stashUserPassword[0].value().empty
            ignoreUnverifiedSSLPeer[0].value() == true
            commitSha1[0].value() == 'sha1'
            includeBuildNumberInKey[0].value() == true
        }
        1 * jobManagement.requirePlugin('stashNotifier')
        1 * jobManagement.logPluginDeprecationWarning('stashNotifier', '1.11.6')
    }

    def 'stashNotifier with default configuration and plugin version 1.9.0'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('stashNotifier', '1.9.0') >> true

        when:
        context.stashNotifier {
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.stashNotifier.StashNotifier'
            children().size() == 5
            stashServerBaseUrl[0].value().empty
            credentialsId[0].value().empty
            ignoreUnverifiedSSLPeer[0].value() == false
            commitSha1[0].value() == ''
            includeBuildNumberInKey[0].value() == false
        }
        1 * jobManagement.requirePlugin('stashNotifier')
        1 * jobManagement.logPluginDeprecationWarning('stashNotifier', '1.11.6')
    }

    def 'stashNotifier with configuration of all parameters and plugin version 1.9.0'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('stashNotifier', '1.9.0') >> true

        when:
        context.stashNotifier {
            serverBaseUrl('test')
            credentialsId('foo')
            commitSha1('sha1')
            keepRepeatedBuilds(true)
            ignoreUnverifiedSSLCertificates(true)
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.stashNotifier.StashNotifier'
            children().size() == 5
            stashServerBaseUrl[0].value() == 'test'
            credentialsId[0].value() == 'foo'
            ignoreUnverifiedSSLPeer[0].value() == true
            commitSha1[0].value() == 'sha1'
            includeBuildNumberInKey[0].value() == true
        }
        1 * jobManagement.requirePlugin('stashNotifier')
        1 * jobManagement.requireMinimumPluginVersion('stashNotifier', '1.9.0')
        1 * jobManagement.logPluginDeprecationWarning('stashNotifier', '1.11.6')
    }

    def 'mavenDeploymentLinker with regex'() {
        when:
        context.mavenDeploymentLinker('.*.tar.gz')

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.mavendeploymentlinker.MavenDeploymentLinkerRecorder'
            regexp[0].value() == '.*.tar.gz'
        }
        1 * jobManagement.requirePlugin('maven-deployment-linker')
    }

    def 'wsCleanup with configuration of all parameters'() {
        when:
        context.wsCleanup {
            includePattern('foo')
            includePattern('bar')
            excludePattern('foo')
            excludePattern('bar')
            deleteDirectories(true)
            cleanWhenSuccess(false)
            cleanWhenUnstable(false)
            cleanWhenFailure(false)
            cleanWhenNotBuilt(false)
            cleanWhenAborted(false)
            failBuildWhenCleanupFails(false)
            deleteCommand('rm')
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.ws__cleanup.WsCleanup'
            patterns.size() == 1
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'.size() == 4
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[0].pattern[0].value() == 'foo'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[0].type[0].value() == 'INCLUDE'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[1].pattern[0].value() == 'bar'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[1].type[0].value() == 'INCLUDE'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[2].pattern[0].value() == 'foo'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[2].type[0].value() == 'EXCLUDE'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[3].pattern[0].value() == 'bar'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[3].type[0].value() == 'EXCLUDE'
            deleteDirs[0].value() == true
            cleanWhenSuccess[0].value() == false
            cleanWhenUnstable[0].value() == false
            cleanWhenFailure[0].value() == false
            cleanWhenNotBuilt[0].value() == false
            cleanWhenAborted[0].value() == false
            notFailBuild[0].value() == true
            externalDelete[0].value() == 'rm'
        }
        1 * jobManagement.requirePlugin('ws-cleanup')
    }

    def 'wsCleanup with default configuration'() {
        when:
        context.wsCleanup()

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.ws__cleanup.WsCleanup'
            patterns.size() == 1
            patterns[0].entry.size() == 0
            deleteDirs[0].value() == false
            cleanWhenSuccess[0].value() == true
            cleanWhenUnstable[0].value() == true
            cleanWhenFailure[0].value() == true
            cleanWhenNotBuilt[0].value() == true
            cleanWhenAborted[0].value() == true
            notFailBuild[0].value() == false
            externalDelete[0].value() == ''
        }
        1 * jobManagement.requirePlugin('ws-cleanup')
    }

    def 'wsCleanup with configuration of all parameters using defaults for boolean parameter'() {
        when:
        context.wsCleanup {
            includePattern('foo')
            includePattern('bar')
            excludePattern('foo')
            excludePattern('bar')
            deleteDirectories()
            cleanWhenSuccess()
            cleanWhenUnstable()
            cleanWhenFailure()
            cleanWhenNotBuilt()
            cleanWhenAborted()
            failBuildWhenCleanupFails()
            deleteCommand('rm')
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.ws__cleanup.WsCleanup'
            patterns.size() == 1
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'.size() == 4
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[0].pattern[0].value() == 'foo'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[0].type[0].value() == 'INCLUDE'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[1].pattern[0].value() == 'bar'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[1].type[0].value() == 'INCLUDE'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[2].pattern[0].value() == 'foo'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[2].type[0].value() == 'EXCLUDE'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[3].pattern[0].value() == 'bar'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[3].type[0].value() == 'EXCLUDE'
            deleteDirs[0].value() == true
            cleanWhenSuccess[0].value() == true
            cleanWhenUnstable[0].value() == true
            cleanWhenFailure[0].value() == true
            cleanWhenNotBuilt[0].value() == true
            cleanWhenAborted[0].value() == true
            notFailBuild[0].value() == false
            externalDelete[0].value() == 'rm'
        }
        1 * jobManagement.requirePlugin('ws-cleanup')
    }

    def 'call s3 without profile'(String profile) {
        when:
        context.s3(profile) {
        }

        then:
        thrown(DslScriptException)

        where:
        profile << [null, '']
    }

    def 'call s3 without source or bucket or with invalid region'(String source, String bucket, String region) {
        when:
        context.s3('test') {
            entry(source, bucket, region)
        }

        then:
        thrown(DslScriptException)

        where:
        source | bucket | region
        null   | 'test' | 'eu-west-1'
        ''     | 'test' | 'eu-west-1'
        'test' | null   | 'eu-west-1'
        'test' | ''     | 'eu-west-1'
        null   | null   | 'eu-west-1'
        ''     | ''     | 'eu-west-1'
        'test' | 'test' | ''
        'test' | 'test' | null
    }

    def 'call s3 with invalid storage class'(String storageClass) {
        when:
        context.s3('test') {
            entry('foo', 'bar', 'eu-west-1') {
                delegate.storageClass(storageClass)
            }
        }

        then:
        thrown(DslScriptException)

        where:
        storageClass << [null, '', 'FOO']
    }

    def 'call s3 with some options'() {
        when:
        context.s3('profile') {
            entry('foo', 'bar', 'us-east-1')
            metadata('key', 'value')
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.s3.S3BucketPublisher'
            children().size() == 3
            profileName[0].value() == 'profile'
            entries.size() == 1
            entries[0].'hudson.plugins.s3.Entry'.size() == 1
            with(entries[0].'hudson.plugins.s3.Entry'[0]) {
                children().size() == 9
                sourceFile[0].value() == 'foo'
                bucket[0].value() == 'bar'
                storageClass[0].value() == 'STANDARD'
                selectedRegion[0].value() == 'us-east-1'
                noUploadOnFailure[0].value() == false
                uploadFromSlave[0].value() == false
                managedArtifacts[0].value() == false
                useServerSideEncryption[0].value() == false
                flatten[0].value() == false
            }
            userMetadata.size() == 1
            userMetadata[0].'hudson.plugins.s3.MetadataPair'.size() == 1
            with(userMetadata[0].'hudson.plugins.s3.MetadataPair'[0]) {
                children().size() == 2
                key[0].value() == 'key'
                value[0].value() == 'value'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('s3', '0.7')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'call s3 with more options'() {
        when:
        context.s3('profile') {
            entry('foo', 'bar', 'eu-west-1')
            entry('bar', 'baz', 'us-east-1') {
                storageClass('REDUCED_REDUNDANCY')
                noUploadOnFailure(true)
                uploadFromSlave(true)
                managedArtifacts(true)
                useServerSideEncryption(true)
                flatten(true)
            }
            metadata('key', 'value')
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.s3.S3BucketPublisher'
            children().size() == 3
            profileName[0].value() == 'profile'
            entries.size() == 1
            entries[0].'hudson.plugins.s3.Entry'.size() == 2
            with(entries[0].'hudson.plugins.s3.Entry'[0]) {
                children().size() == 9
                sourceFile[0].value() == 'foo'
                bucket[0].value() == 'bar'
                storageClass[0].value() == 'STANDARD'
                selectedRegion[0].value() == 'eu-west-1'
                noUploadOnFailure[0].value() == false
                uploadFromSlave[0].value() == false
                managedArtifacts[0].value() == false
                useServerSideEncryption[0].value() == false
                flatten[0].value() == false
            }
            with(entries[0].'hudson.plugins.s3.Entry'[1]) {
                children().size() == 9
                sourceFile[0].value() == 'bar'
                bucket[0].value() == 'baz'
                storageClass[0].value() == 'REDUCED_REDUNDANCY'
                selectedRegion[0].value() == 'us-east-1'
                noUploadOnFailure[0].value() == true
                uploadFromSlave[0].value() == true
                managedArtifacts[0].value() == true
                useServerSideEncryption[0].value() == true
                flatten[0].value() == true
            }
            userMetadata.size() == 1
            userMetadata[0].'hudson.plugins.s3.MetadataPair'.size() == 1
            with(userMetadata[0].'hudson.plugins.s3.MetadataPair'[0]) {
                children().size() == 2
                key[0].value() == 'key'
                value[0].value() == 'value'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('s3', '0.7')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'call flexible publish'() {
        when:
        context.flexiblePublish {
            conditionalAction {
                condition {
                    stringsMatch('foo', 'bar', false)
                }
                publishers {
                    mailer('test@test.com')
                }
            }
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher'
            children().size() == 1
            publishers[0].children().size == 1
            with(publishers[0].children()[0]) {
                name() == 'org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher'
                children().size() == 3
                condition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition'
                condition[0].arg1[0].value() == 'foo'
                condition[0].arg2[0].value() == 'bar'
                condition[0].ignoreCase[0].value() == false
                runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'
                runner[0].value().empty
                publisherList[0].children().size() == 1
                with(publisherList[0]) {
                    children()[0].name() == 'hudson.tasks.Mailer'
                    children()[0].recipients[0].value() == 'test@test.com'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('flexible-publish', '0.13')
    }

    def 'call flexible publish with build step'() {
        when:
        context.flexiblePublish {
            conditionalAction {
                condition {
                    stringsMatch('foo', 'bar', false)
                }
                steps {
                    shell('echo hello')
                }
            }
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher'
            children().size() == 1
            publishers[0].children().size == 1
            with(publishers[0].children()[0]) {
                name() == 'org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher'
                children().size() == 3
                condition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition'
                condition[0].arg1[0].value() == 'foo'
                condition[0].arg2[0].value() == 'bar'
                condition[0].ignoreCase[0].value() == false
                runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'
                runner[0].value().empty
                publisherList[0].children().size() == 1
                with(publisherList[0]) {
                    children()[0].name() == 'hudson.tasks.Shell'
                    children()[0].command[0].value() == 'echo hello'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('flexible-publish', '0.13')
        1 * jobManagement.requirePlugin('any-buildstep')
    }

    def 'call flexible publish with multiple actions'() {
        when:
        context.flexiblePublish {
            conditionalAction {
                condition {
                    stringsMatch('foo', 'bar', false)
                }
                steps {
                    shell('echo hello')
                }
                publishers {
                    wsCleanup()
                }
            }
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher'
            children().size() == 1
            publishers[0].children().size == 1
            with(publishers[0].children()[0]) {
                name() == 'org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher'
                children().size() == 3
                condition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition'
                condition[0].arg1[0].value() == 'foo'
                condition[0].arg2[0].value() == 'bar'
                condition[0].ignoreCase[0].value() == false
                runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'
                runner[0].value().empty
                publisherList[0].children().size() == 2
                with(publisherList[0]) {
                    children()[0].name() == 'hudson.tasks.Shell'
                    children()[1].name() == 'hudson.plugins.ws__cleanup.WsCleanup'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('flexible-publish', '0.13')
        1 * jobManagement.requirePlugin('any-buildstep')
    }

    def 'call flexible publish without condition'() {
        when:
        context.flexiblePublish {
            conditionalAction {
                steps {
                    shell('echo hello')
                }
            }
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher'
            children().size() == 1
            publishers[0].children().size == 1
            with(publishers[0].children()[0]) {
                name() == 'org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher'
                children().size() == 3
                condition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
                condition[0].children().size() == 0
                runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'
                runner[0].value().empty
                publisherList[0].children().size() == 1
                with(publisherList[0]) {
                    children()[0].name() == 'hudson.tasks.Shell'
                    children()[0].command[0].value() == 'echo hello'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('flexible-publish', '0.13')
    }

    def 'call flexible publish with multiple conditional actions'() {
        when:
        context.flexiblePublish {
            conditionalAction {
                steps {
                    shell('echo hello')
                }
            }
            conditionalAction {
                condition {
                    stringsMatch('foo', 'bar', false)
                }
                runner('DontRun')
                publishers {
                    wsCleanup()
                }
            }
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher'
            children().size() == 1
            publishers[0].children().size == 2
            with(publishers[0].children()[0]) {
                name() == 'org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher'
                children().size() == 3
                condition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
                condition[0].children().size() == 0
                runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'
                runner[0].value().empty
                with(publisherList[0]) {
                    children().size() == 1
                    children()[0].name() == 'hudson.tasks.Shell'
                    children()[0].command[0].value() == 'echo hello'
                }
            }
            with(publishers[0].children()[1]) {
                name() == 'org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher'
                children().size() == 3
                condition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition'
                condition[0].arg1[0].value() == 'foo'
                condition[0].arg2[0].value() == 'bar'
                condition[0].ignoreCase[0].value() == false
                runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$DontRun'
                runner[0].value().empty
                with(publisherList[0]) {
                    children().size() == 1
                    children()[0].name() == 'hudson.plugins.ws__cleanup.WsCleanup'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('flexible-publish', '0.13')
    }

    def 'call flexible publish without action'() {
        when:
        context.flexiblePublish {
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher'
            children().size() == 1
            publishers[0].value().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('flexible-publish', '0.13')
    }

    def 'call flexible publish with matrix options'() {
        given:
        context = new PublisherContext(jobManagement, Mock(MatrixJob))

        when:
        context.flexiblePublish {
            conditionalAction {
                aggregationCondition {
                    stringsMatch('foo', 'bar', false)
                }
                aggregationRunner('DontRun')
                steps {
                    shell('echo hello')
                }
            }
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher'
            children().size() == 1
            publishers[0].children().size == 1
            with(publishers[0].children()[0]) {
                name() == 'org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher'
                children().size() == 5
                condition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
                condition[0].children().size() == 0
                runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'
                runner[0].value().empty
                with(aggregationCondition[0]) {
                    attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition'
                    arg1[0].value() == 'foo'
                    arg2[0].value() == 'bar'
                    ignoreCase[0].value() == false
                }
                with(aggregationRunner[0]) {
                    attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$DontRun'
                    value().empty
                }
                with(publisherList[0]) {
                    children().size() == 1
                    children()[0].name() == 'hudson.tasks.Shell'
                    children()[0].command[0].value() == 'echo hello'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('flexible-publish', '0.13')
    }

    def 'call flexible publish aggregationRunner not in matrix job'() {
        when:
        context.flexiblePublish {
            conditionalAction {
                aggregationRunner('Fail')
            }
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'can only be using in matrix jobs'
    }

    def 'call flexible publish aggregationCondition not in matrix job'() {
        when:
        context.flexiblePublish {
            conditionalAction {
                aggregationCondition {
                    always()
                }
            }
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'can only be using in matrix jobs'
    }

    def 'call post build scripts with minimal options'() {
        when:
        context.postBuildScripts {
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.postbuildscript.PostBuildScript'
            children().size() == 4
            buildSteps[0].children().size == 0
            scriptOnlyIfSuccess[0].value() == true
            scriptOnlyIfFailure[0].value() == false
            markBuildUnstable[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('postbuildscript', '0.17')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'call post build scripts with all options'() {
        when:
        context.postBuildScripts {
            steps {
                shell('echo TEST')
            }
            onlyIfBuildSucceeds(value)
            onlyIfBuildFails(value)
            markBuildUnstable(value)
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.postbuildscript.PostBuildScript'
            children().size() == 4
            buildSteps[0].children().size == 1
            buildSteps[0].children()[0].name() == 'hudson.tasks.Shell'
            scriptOnlyIfSuccess[0].value() == value
            scriptOnlyIfFailure[0].value() == value
            markBuildUnstable[0].value() == value
        }
        1 * jobManagement.requireMinimumPluginVersion('postbuildscript', '0.17')
        1 * jobManagement.logDeprecationWarning()

        where:
        value << [true, false]
    }

    def 'call post build scripts with minimal options and matrix job'() {
        setup:
        Item item = new MatrixJob(jobManagement, 'test')
        PublisherContext context = new PublisherContext(jobManagement, item)

        when:
        context.postBuildScripts {
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.postbuildscript.PostBuildScript'
            children().size() == 5
            buildSteps[0].children().size == 0
            scriptOnlyIfSuccess[0].value() == true
            scriptOnlyIfFailure[0].value() == false
            markBuildUnstable[0].value() == false
            executeOn[0].value() == 'BOTH'
        }
        1 * jobManagement.requireMinimumPluginVersion('postbuildscript', '0.17')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'call post build scripts with all options and matrix job'() {
        setup:
        Item item = new MatrixJob(jobManagement, 'test')
        PublisherContext context = new PublisherContext(jobManagement, item)

        when:
        context.postBuildScripts {
            steps {
                shell('echo TEST')
            }
            onlyIfBuildSucceeds(false)
            onlyIfBuildFails()
            markBuildUnstable()
            executeOn(mode)
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.postbuildscript.PostBuildScript'
            children().size() == 5
            buildSteps[0].children().size == 1
            buildSteps[0].children()[0].name() == 'hudson.tasks.Shell'
            scriptOnlyIfSuccess[0].value() == false
            scriptOnlyIfFailure[0].value() == true
            markBuildUnstable[0].value() == true
            executeOn[0].value() == mode
        }
        1 * jobManagement.requireMinimumPluginVersion('postbuildscript', '0.17')
        1 * jobManagement.logDeprecationWarning()

        where:
        mode << ['MATRIX', 'AXES', 'BOTH']
    }

    def 'call sonar with no options'() {
        when:
        context.sonar()

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.sonar.SonarPublisher'
            children().size() == 10
            jdk[0].value() == '(Inherit From Job)'
            branch[0].value() == ''
            language[0].value().empty
            mavenOpts[0].value().empty
            jobAdditionalProperties[0].value().empty
            mavenInstallationName[0].value() == '(Inherit From Job)'
            rootPom[0].value().empty
            settings[0].value().empty
            settings[0].@class == 'jenkins.mvn.DefaultSettingsProvider'
            globalSettings[0].value().empty
            globalSettings[0].@class == 'jenkins.mvn.DefaultGlobalSettingsProvider'
            usePrivateRepository[0].value() == false
        }
        1 * jobManagement.requirePlugin('sonar')
    }

    def 'call sonar with all options'() {
        when:
        context.sonar {
            installationName('sonarTest')
            branch('test')
            additionalProperties('-Dtest=test')
            jdk('myJDK')
            mavenInstallation('myMaven')
            overrideTriggers {
                skipIfEnvironmentVariable('FOO')
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.sonar.SonarPublisher'
            children().size() == 12
            installationName[0].value() == 'sonarTest'
            jdk[0].value() == 'myJDK'
            branch[0].value() == 'test'
            language[0].value().empty
            mavenOpts[0].value().empty
            jobAdditionalProperties[0].value() == '-Dtest=test'
            mavenInstallationName[0].value() == 'myMaven'
            rootPom[0].value().empty
            settings[0].value().empty
            settings[0].@class == 'jenkins.mvn.DefaultSettingsProvider'
            globalSettings[0].value().empty
            globalSettings[0].@class == 'jenkins.mvn.DefaultGlobalSettingsProvider'
            usePrivateRepository[0].value() == false
            triggers[0].children().size() == 3
            triggers[0].skipScmCause[0].value() == false
            triggers[0].skipUpstreamCause[0].value() == false
            triggers[0].envVar[0].value() == 'FOO'
        }
        1 * jobManagement.requirePlugin('sonar')
    }

    def 'call plotPlugin with some basic args'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                propertiesFile('data.prop')
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.PropertiesSeries'[0]) {
                    children().size() == 3
                    file[0].value() == 'data.prop'
                    label[0].value() == ''
                    fileType[0].value() == 'properties'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with all args'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                title('plot title')
                yAxis('yaxis title')
                numberOfBuilds(42)
                useDescriptions()
                keepRecords()
                excludeZero()
                logarithmic()
                propertiesFile('data.prop')
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value() == 'plot title'
                yaxis[0].value() == 'yaxis title'
                group[0].value() == 'my group'
                numBuilds[0].value() == 42
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == true
                keepRecords[0].value() == true
                exclZero[0].value() == true
                logarithmic[0].value() == true
                with(series[0].'hudson.plugins.plot.PropertiesSeries'[0]) {
                    children().size() == 3
                    file[0].value() == 'data.prop'
                    label[0].value() == ''
                    fileType[0].value() == 'properties'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with all chart styles'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                style(chart)
                propertiesFile('data.prop') {
                    label('some label')
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == chart
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.PropertiesSeries'[0]) {
                    children().size() == 3
                    file[0].value() == 'data.prop'
                    label[0].value() == 'some label'
                    fileType[0].value() == 'properties'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')

        where:
        chart << ['area', 'bar', 'bar3d', 'line', 'line3d', 'stackedArea', 'stackedbar', 'stackedbar3d', 'waterfall']
    }

    def 'call plotPlugin with a xml series'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                xmlFile('data.prop')
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.XMLSeries'[0]) {
                    children().size() == 6
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'xml'
                    label[0].value().empty
                    nodeTypeString[0].value() == 'NODESET'
                    url[0].value() == ''
                    xpathString[0].value() == ''
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with full xml series'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                xmlFile('data.prop') {
                    nodeType('NODE')
                    url('http://somewhere')
                    xpath('an xpath string')
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.XMLSeries'[0]) {
                    children().size() == 6
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'xml'
                    label[0].value().empty
                    nodeTypeString[0].value() == 'NODE'
                    url[0].value() == 'http://somewhere'
                    xpathString[0].value() == 'an xpath string'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with a csv series'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                csvFile('data.prop')
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.CSVSeries'[0]) {
                    children().size() == 7
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'csv'
                    label[0].value().empty
                    inclusionFlag[0].value() == 'OFF'
                    exclusionValues[0].value() == ''
                    url[0].value() == ''
                    displayTableFlag[0].value() == false
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with full csv series'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                csvFile('data.prop') {
                    url('http://somewhere')
                    showTable()
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.CSVSeries'[0]) {
                    children().size() == 7
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'csv'
                    label[0].value().empty
                    inclusionFlag[0].value() == 'OFF'
                    exclusionValues[0].value() == ''
                    url[0].value() == 'http://somewhere'
                    displayTableFlag[0].value() == true
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with csv series using single includeColumns(str)'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                csvFile('data.prop') {
                    includeColumns('foo')
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.CSVSeries'[0]) {
                    children().size() == 8
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'csv'
                    label[0].value().empty
                    inclusionFlag[0].value() == 'INCLUDE_BY_STRING'
                    exclusionValues[0].value() == 'foo'
                    url[0].value() == ''
                    displayTableFlag[0].value() == false
                    with(strExclusionSet[0]) {
                        children().size() == 1
                        string[0].value() == 'foo'
                    }
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with csv series using multiple includeColumns(str)'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                csvFile('data.prop') {
                    includeColumns('foo')
                    includeColumns('bar', 'woo')
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.CSVSeries'[0]) {
                    children().size() == 8
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'csv'
                    label[0].value().empty
                    inclusionFlag[0].value() == 'INCLUDE_BY_STRING'
                    exclusionValues[0].value() == 'foo,bar,woo'
                    url[0].value() == ''
                    displayTableFlag[0].value() == false
                    with(strExclusionSet[0]) {
                        children().size() == 3
                        string[0].value() == 'foo'
                        string[1].value() == 'bar'
                        string[2].value() == 'woo'
                    }
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with csv series using single excludeColumns(str)'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                csvFile('data.prop') {
                    excludeColumns('foo')
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.CSVSeries'[0]) {
                    children().size() == 8
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'csv'
                    label[0].value().empty
                    inclusionFlag[0].value() == 'EXCLUDE_BY_STRING'
                    exclusionValues[0].value() == 'foo'
                    url[0].value() == ''
                    displayTableFlag[0].value() == false
                    with(strExclusionSet[0]) {
                        children().size() == 1
                        string[0].value() == 'foo'
                    }
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with csv series using multiple excludeColumns(str)'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                csvFile('data.prop') {
                    excludeColumns('foo')
                    excludeColumns('bar', 'woo')
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.CSVSeries'[0]) {
                    children().size() == 8
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'csv'
                    label[0].value().empty
                    inclusionFlag[0].value() == 'EXCLUDE_BY_STRING'
                    exclusionValues[0].value() == 'foo,bar,woo'
                    url[0].value() == ''
                    displayTableFlag[0].value() == false
                    with(strExclusionSet[0]) {
                        children().size() == 3
                        string[0].value() == 'foo'
                        string[1].value() == 'bar'
                        string[2].value() == 'woo'
                    }
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with csv series using single includeColumns(int)'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                csvFile('data.prop') {
                    includeColumns(1)
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.CSVSeries'[0]) {
                    children().size() == 8
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'csv'
                    label[0].value().empty
                    inclusionFlag[0].value() == 'INCLUDE_BY_COLUMN'
                    exclusionValues[0].value() == '1'
                    url[0].value() == ''
                    displayTableFlag[0].value() == false
                    with(colExclusionSet[0]) {
                        children().size() == 1
                    }
                    colExclusionSet[0].'int'[0].value() == '1'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with csv series using multiple includeColumns(int)'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                csvFile('data.prop') {
                    includeColumns(1)
                    includeColumns(3, 6)
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.CSVSeries'[0]) {
                    children().size() == 8
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'csv'
                    label[0].value().empty
                    inclusionFlag[0].value() == 'INCLUDE_BY_COLUMN'
                    exclusionValues[0].value() == '1,3,6'
                    url[0].value() == ''
                    displayTableFlag[0].value() == false
                    with(colExclusionSet[0]) {
                        children().size() == 3
                    }
                    colExclusionSet[0].'int'[0].value() == '1'
                    colExclusionSet[0].'int'[1].value() == '3'
                    colExclusionSet[0].'int'[2].value() == '6'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with csv series using single excludeColumns(int)'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                csvFile('data.prop') {
                    excludeColumns(1)
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.CSVSeries'[0]) {
                    children().size() == 8
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'csv'
                    label[0].value().empty
                    inclusionFlag[0].value() == 'EXCLUDE_BY_COLUMN'
                    exclusionValues[0].value() == '1'
                    url[0].value() == ''
                    displayTableFlag[0].value() == false
                    with(colExclusionSet[0]) {
                        children().size() == 1
                    }
                    colExclusionSet[0].'int'[0].value() == '1'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with csv series using multiple excludeColumns(int)'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                csvFile('data.prop') {
                    excludeColumns(1)
                    excludeColumns(3, 6)
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.plot.PlotPublisher'
            children().size() == 1
            with(plots.'hudson.plugins.plot.Plot'[0]) {
                children().size() == 12
                title[0].value().empty
                yaxis[0].value().empty
                group[0].value() == 'my group'
                numBuilds[0].value().empty
                csvFileName[0].value() == 'some.csv'
                csvLastModification[0].value() == 0
                style[0].value() == 'line'
                useDescr[0].value() == false
                keepRecords[0].value() == false
                exclZero[0].value() == false
                logarithmic[0].value() == false
                with(series[0].'hudson.plugins.plot.CSVSeries'[0]) {
                    children().size() == 8
                    file[0].value() == 'data.prop'
                    fileType[0].value() == 'csv'
                    label[0].value().empty
                    inclusionFlag[0].value() == 'EXCLUDE_BY_COLUMN'
                    exclusionValues[0].value() == '1,3,6'
                    url[0].value() == ''
                    displayTableFlag[0].value() == false
                    with(colExclusionSet[0]) {
                        children().size() == 3
                    }
                    colExclusionSet[0].'int'[0].value() == '1'
                    colExclusionSet[0].'int'[1].value() == '3'
                    colExclusionSet[0].'int'[2].value() == '6'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }

    def 'call plotPlugin with csv series mixing exclude types'() {
        when:
        context.plotBuildData {
            plot('my group', 'some.csv') {
                csvFile('data.prop') {
                    "$type0"(data0)
                    "$type1"(data1)
                }
            }
        }

        then:
        thrown(DslScriptException)

        where:
        type0            | data0 | type1            | data1
        'includeColumns' | 0     | 'includeColumns' | 'foo'
        'includeColumns' | 'foo' | 'includeColumns' | 0
        'excludeColumns' | 0     | 'excludeColumns' | 'foo'
        'excludeColumns' | 'foo' | 'excludeColumns' | 0
        'includeColumns' | 0     | 'excludeColumns' | 0
        'excludeColumns' | 0     | 'includeColumns' | 0
        'includeColumns' | 'foo' | 'excludeColumns' | 'foo'
        'excludeColumns' | 'foo' | 'includeColumns' | 'foo'
    }

    def 'call plotPlugin without group'() {
        when:
        context.plotBuildData {
            plot(group, 'some.csv') {
                propertiesFile('data.prop')
            }
        }

        then:
        thrown(DslScriptException)

        where:
        group << [null, '']
    }

    def 'call plotPlugin without data store'() {
        when:
        context.plotBuildData {
            plot('my group', dataStore) {
                propertiesFile('data.prop')
            }
        }

        then:
        thrown(DslScriptException)

        where:
        dataStore << [null, '']
    }

    def 'call plotPlugin without file name'() {
        when:
        context.plotBuildData {
            plot('my group', 'test.csv') {
                propertiesFile(fileName)
            }
        }

        then:
        thrown(DslScriptException)

        where:
        fileName << [null, '']
    }

    def 'call plotPlugin with invalid style'() {
        when:
        context.plotBuildData {
            plot('my group', 'test.csv') {
                style('foo')
                propertiesFile('data.prop')
            }
        }

        then:
        thrown(DslScriptException)
    }

    def 'retryBuild with no options'() {
        when:
        context.retryBuild()

        then:
        with(context.publisherNodes[0]) {
            name() == 'com.chikli.hudson.plugin.naginator.NaginatorPublisher'
            children().size() == 6
            regexpForRerun[0].value().empty
            rerunIfUnstable[0].value() == false
            rerunMatrixPart[0].value() == false
            checkRegexp[0].value() == false
            maxSchedule[0].value() == 0
            delay[0].@class == 'com.chikli.hudson.plugin.naginator.ProgressiveDelay'
            delay[0].children().size() == 2
            delay[0].increment[0].value() == 300
            delay[0].max[0].value() == 10800
        }
        1 * jobManagement.requireMinimumPluginVersion('naginator', '1.15')
    }

    def 'retryBuild with all options'() {
        when:
        context.retryBuild {
            rerunIfUnstable()
            retryLimit(3)
            fixedDelay(30)
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'com.chikli.hudson.plugin.naginator.NaginatorPublisher'
            children().size() == 6
            regexpForRerun[0].value().empty
            rerunIfUnstable[0].value() == true
            rerunMatrixPart[0].value() == false
            checkRegexp[0].value() == false
            maxSchedule[0].value() == 3
            delay[0].@class == 'com.chikli.hudson.plugin.naginator.FixedDelay'
            delay[0].children().size() == 1
            delay[0].delay[0].value() == 30
        }
        1 * jobManagement.requireMinimumPluginVersion('naginator', '1.15')
    }

    def 'publishBuild with no options'() {
        when:
        context.publishBuild()

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.build__publisher.BuildPublisher'
            children().size() == 2
            publishUnstableBuilds[0].value() == true
            publishFailedBuilds[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('build-publisher', '1.20')
    }

    def 'phabricatorNotifier with no options'() {
        when:
        context.phabricatorNotifier()

        then:
        with(context.publisherNodes[0]) {
            name() == 'com.uber.jenkins.phabricator.PhabricatorNotifier'
            children().size() == 6
            commentOnSuccess[0].value() == false
            commentWithConsoleLinkOnFailure[0].value() == false
            commentFile[0].value() == '.phabricator-comment'
            commentSize[0].value() == 1000
            preserveFormatting[0].value() == false
            uberallsEnabled[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('phabricator-plugin', '1.8.1')
    }

    def 'phabricatorNotifier with all options'() {
        when:
        context.phabricatorNotifier {
            commentOnSuccess()
            commentWithConsoleLinkOnFailure()
            commentFile('.my-comment-file')
            commentSize(2000)
            preserveFormatting()
            enableUberalls(false)
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'com.uber.jenkins.phabricator.PhabricatorNotifier'
            children().size() == 6
            commentOnSuccess[0].value() == true
            commentWithConsoleLinkOnFailure[0].value() == true
            commentFile[0].value() == '.my-comment-file'
            commentSize[0].value() == 2000
            preserveFormatting[0].value() == true
            uberallsEnabled[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('phabricator-plugin', '1.8.1')
    }

    def 'publishBuild with all options'() {
        when:
        context.publishBuild {
            publishUnstable(false)
            publishFailed(false)
            discardOldBuilds(5, 3)
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.build__publisher.BuildPublisher'
            children().size() == 3
            publishUnstableBuilds[0].value() == false
            publishFailedBuilds[0].value() == false
            logRotator[0].children().size() == 4
            logRotator[0].daysToKeep[0].value() == 5
            logRotator[0].numToKeep[0].value() == 3
            logRotator[0].artifactDaysToKeep[0].value() == -1
            logRotator[0].artifactNumToKeep[0].value() == -1
        }
        1 * jobManagement.requireMinimumPluginVersion('build-publisher', '1.20')
    }

    def 'mattermost notification with no options'() {
        when:
        context.mattermost()

        then:
        with(context.publisherNodes[0]) {
            name() == 'jenkins.plugins.mattermost.MattermostNotifier'
            children().size() == 15
            endpoint[0].value() == ''
            room[0].value() == ''
            icon[0].value() == ''
            customMessage[0].value() == ''
            startNotification[0].value() == false
            notifySuccess[0].value() == false
            notifyAborted[0].value() == false
            notifyNotBuilt[0].value() == false
            notifyUnstable[0].value() == false
            notifyFailure[0].value() == false
            notifyBackToNormal[0].value() == false
            notifyRepeatedFailure[0].value() == false
            includeTestSummary[0].value() == false
            showCommitList[0].value() == false
            includeCustomMessage[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('mattermost', '1.5.0')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'mattermost notification with all options'() {
        when:
        context.mattermost {
            endpoint('one')
            room('two')
            icon('three')
            customMessage('four')
            notifyBuildStart()
            notifySuccess()
            notifyAborted()
            notifyNotBuilt()
            notifyUnstable()
            notifyFailure()
            notifyBackToNormal()
            notifyRepeatedFailure()
            includeTestSummary()
            showCommitList()
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'jenkins.plugins.mattermost.MattermostNotifier'
            children().size() == 15
            endpoint[0].value() == 'one'
            room[0].value() == 'two'
            icon[0].value() == 'three'
            customMessage[0].value() == 'four'
            startNotification[0].value() == true
            notifySuccess[0].value() == true
            notifyAborted[0].value() == true
            notifyNotBuilt[0].value() == true
            notifyUnstable[0].value() == true
            notifyFailure[0].value() == true
            notifyBackToNormal[0].value() == true
            notifyRepeatedFailure[0].value() == true
            includeTestSummary[0].value() == true
            showCommitList[0].value() == true
            includeCustomMessage[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('mattermost', '1.5.0')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'call publishOverSsh without server'() {
        when:
        context.publishOverSsh(null)

        then:
        thrown(DslScriptException)
    }

    def 'call publishOverSsh without transferSet'() {
        when:
        context.publishOverSsh {
            server('server-name') {
            }
        }

        then:
        thrown(DslScriptException)
    }

    def 'call publishOverSsh without sourceFiles and execCommand'() {
        when:
        context.publishOverSsh {
            server('server-name') {
                transferSet {
                }
            }
        }

        then:
        thrown(DslScriptException)
    }

    def 'call publishOverSsh with minimal configuration and check the default values'() {
        when:
        context.publishOverSsh {
            server('server-name') {
                transferSet {
                    sourceFiles('file')
                    execCommand('command')
                }
            }
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'jenkins.plugins.publish__over__ssh.BapSshPublisherPlugin'
            consolePrefix[0].value() == 'SSH: '
            with(delegate.delegate[0]) {
                with(publishers[0]) {
                    children().size() == 1
                    with(delegate.'jenkins.plugins.publish__over__ssh.BapSshPublisher'[0]) {
                        configName[0].value() == 'server-name'
                        verbose[0].value() == false
                        with(transfers[0]) {
                            children().size() == 1
                            with(delegate.'jenkins.plugins.publish__over__ssh.BapSshTransfer'[0]) {
                                remoteDirectory[0].value() == ''
                                sourceFiles[0].value() == 'file'
                                excludes[0].value() == ''
                                removePrefix[0].value() == ''
                                remoteDirectorySDF[0].value() == false
                                flatten[0].value() == false
                                cleanRemote[0].value() == false
                                noDefaultExcludes[0].value() == false
                                makeEmptyDirs[0].value() == false
                                patternSeparator[0].value() == '[, ]+'
                                execCommand[0].value() == 'command'
                                execTimeout[0].value() == 120000
                                usePty[0].value() == false
                            }
                        }
                        useWorkspaceInPromotion[0].value() == false
                        usePromotionTimestamp[0].value() == false
                    }
                }
                continueOnError[0].value() == false
                failOnError[0].value() == false
                alwaysPublishFromMaster[0].value() == false
                hostConfigurationAccess[0].@class == 'jenkins.plugins.publish_over_ssh.BapSshPublisherPlugin'
                hostConfigurationAccess[0].@reference == '../..'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('publish-over-ssh', '1.12')
    }

    def 'call publishOverSsh with complex configuration'() {
        when:
        context.publishOverSsh {
            server('my-server-01') {
                verbose()
                credentials('user01') {
                    pathToKey('path01')
                }
                retry(10, 10000)
                label('server-01')
                transferSet {
                    sourceFiles('files')
                    execCommand('command')
                    removePrefix('prefix')
                    remoteDirectory('directory')
                    excludeFiles('exclude files')
                    patternSeparator('[| ]+')
                    noDefaultExcludes(true)
                    makeEmptyDirs()
                    flattenFiles()
                    remoteDirIsDateFormat()
                    execTimeout(11111)
                    execInPty()
                }
                transferSet {
                    sourceFiles('files2')
                    execCommand('commands2')
                }
            }
            server('my-server-02') {
                verbose(true)
                credentials('user2') {
                    key('key')
                }
                retry(20, 20000)
                label('server-02')
                transferSet {
                    sourceFiles('files3')
                    execCommand('commands3')
                }
            }
            continueOnError()
            failOnError()
            alwaysPublishFromMaster()
            parameterizedPublishing('PARAMETER')
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'jenkins.plugins.publish__over__ssh.BapSshPublisherPlugin'
            consolePrefix[0].value() == 'SSH: '
            with(delegate.delegate[0]) {
                with(publishers[0]) {
                    children().size() == 2
                    with(delegate.'jenkins.plugins.publish__over__ssh.BapSshPublisher'[0]) {
                        configName[0].value() == 'my-server-01'
                        verbose[0].value() == true
                        with(transfers[0]) {
                            children().size() == 2
                            with(delegate.'jenkins.plugins.publish__over__ssh.BapSshTransfer'[0]) {
                                remoteDirectory[0].value() == 'directory'
                                sourceFiles[0].value() == 'files'
                                excludes[0].value() == 'exclude files'
                                removePrefix[0].value() == 'prefix'
                                remoteDirectorySDF[0].value() == true
                                flatten[0].value() == true
                                cleanRemote[0].value() == false
                                noDefaultExcludes[0].value() == true
                                makeEmptyDirs[0].value() == true
                                patternSeparator[0].value() == '[| ]+'
                                execCommand[0].value() == 'command'
                                execTimeout[0].value() == 11111
                                usePty[0].value() == true
                            }
                            with(delegate.'jenkins.plugins.publish__over__ssh.BapSshTransfer'[1]) {
                                sourceFiles[0].value() == 'files2'
                                execCommand[0].value() == 'commands2'
                            }
                        }
                        with(retry[0]) {
                            delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshRetry'
                            retries[0].value() == 10
                            retryDelay[0].value() == 10000
                        }
                        with(label[0]) {
                            delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshPublisherLabel'
                            label[0].value() == 'server-01'
                        }
                        with(credentials[0]) {
                            delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshCredentials'
                            secretPassphrase[0].value() == ''
                            key[0].value() == ''
                            keyPath[0].value() == 'path01'
                            username[0].value() == 'user01'
                        }
                    }
                    with(delegate.'jenkins.plugins.publish__over__ssh.BapSshPublisher'[1]) {
                        configName[0].value() == 'my-server-02'
                        verbose[0].value() == true
                        with(transfers[0]) {
                            children().size() == 1
                            with(delegate.'jenkins.plugins.publish__over__ssh.BapSshTransfer'[0]) {
                                sourceFiles[0].value() == 'files3'
                                execCommand[0].value() == 'commands3'
                            }
                        }
                        with(retry[0]) {
                            delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshRetry'
                            retries[0].value() == 20
                            retryDelay[0].value() == 20000
                        }
                        with(label[0]) {
                            delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshPublisherLabel'
                            label[0].value() == 'server-02'
                        }
                        with(credentials[0]) {
                            delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshCredentials'
                            secretPassphrase[0].value() == ''
                            key[0].value() == 'key'
                            keyPath[0].value() == ''
                            username[0].value() == 'user2'
                        }
                    }
                }
                continueOnError[0].value() == true
                failOnError[0].value() == true
                alwaysPublishFromMaster[0].value() == true
                hostConfigurationAccess[0].@class == 'jenkins.plugins.publish_over_ssh.BapSshPublisherPlugin'
                hostConfigurationAccess[0].@reference == '../..'
                with(paramPublish[0]) {
                    delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshParamPublish'
                    parameterName[0].value() == 'PARAMETER'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('publish-over-ssh', '1.12')
    }

    def 'crittercismDsymUpload with no options'() {
        when:
        context.crittercismDsymUpload {
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.crittercism__dsym.CrittercismDsymRecorder'
            children().size() == 3
            apiKey[0].value() == ''
            appID[0].value() == ''
            filePath[0].value() == ''
        }
        1 * jobManagement.requireMinimumPluginVersion('crittercism-dsym', '1.1')
    }

    def 'crittercismDsymUpload with all options'() {
        when:
        context.crittercismDsymUpload {
            apiKey('theKey')
            appID('theId')
            filePath('thePath')
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.crittercism__dsym.CrittercismDsymRecorder'
            children().size() == 3
            apiKey[0].value() == 'theKey'
            appID[0].value() == 'theId'
            filePath[0].value() == 'thePath'
        }
        1 * jobManagement.requireMinimumPluginVersion('crittercism-dsym', '1.1')
    }

    def 'joinTrigger with no options'() {
        when:
        context.joinTrigger {
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'join.JoinTrigger'
            children().size() == 3
            joinProjects[0].value() == ''
            joinPublishers[0].value().empty
            evenIfDownstreamUnstable[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('join', '1.15')
        1 * jobManagement.logPluginDeprecationWarning('join', '1.21')
    }

    def 'joinTrigger with all options'() {
        when:
        context.joinTrigger {
            projects('one')
            projects('two', 'three')
            publishers {
                downstreamParameterized {
                    trigger('upload-to-staging') {
                        parameters {
                            currentBuild()
                        }
                    }
                }
            }
            evenIfDownstreamUnstable()
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'join.JoinTrigger'
            children().size() == 3
            joinProjects[0].value() == 'one, two, three'
            with(joinPublishers[0]) {
                children().size() == 1
                children()[0].name() == 'hudson.plugins.parameterizedtrigger.BuildTrigger'
                children()[0].children().size() == 1
            }
            evenIfDownstreamUnstable[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('join', '1.15')
        1 * jobManagement.logPluginDeprecationWarning('join', '1.21')
    }

    def 'joinTrigger with no options and plugin version 1.20'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('join', '1.20') >> true

        when:
        context.joinTrigger {
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'join.JoinTrigger'
            children().size() == 3
            joinProjects[0].value() == ''
            joinPublishers[0].value().empty
            with(resultThreshold[0]) {
                children().size() == 4
                name[0].value() == 'SUCCESS'
                ordinal[0].value() == 0
                color[0].value() == 'BLUE'
                completeBuild[0].value() == true
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('join', '1.15')
        1 * jobManagement.logPluginDeprecationWarning('join', '1.21')
    }

    def 'joinTrigger with all options and plugin version 1.20'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('join', '1.20') >> true

        when:
        context.joinTrigger {
            projects('one')
            projects('two', 'three')
            publishers {
                downstreamParameterized {
                    trigger('upload-to-staging') {
                        parameters {
                            currentBuild()
                        }
                    }
                }
            }
            resultThreshold('FAILURE')
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'join.JoinTrigger'
            children().size() == 3
            joinProjects[0].value() == 'one, two, three'
            with(joinPublishers[0]) {
                children().size() == 1
                children()[0].name() == 'hudson.plugins.parameterizedtrigger.BuildTrigger'
                children()[0].children().size() == 1
            }
            with(resultThreshold[0]) {
                children().size() == 4
                name[0].value() == 'FAILURE'
                ordinal[0].value() == 2
                color[0].value() == 'RED'
                completeBuild[0].value() == true
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('join', '1.15')
        1 * jobManagement.requireMinimumPluginVersion('join', '1.20')
        1 * jobManagement.logPluginDeprecationWarning('join', '1.21')
    }

    def 'joinTrigger with unsupported publisher'() {
        when:
        context.joinTrigger {
            publishers {
                artifactDeployer {}
            }
        }

        then:
        thrown(DslScriptException)
    }

    def 'debianPackage with no options'() {
        when:
        context.debianPackage(repoId)

        then:
        thrown(DslScriptException)

        where:
        repoId << [null, '']
    }

    def 'debianPackage with repoId only'() {
        when:
        context.debianPackage('debian-squeeze') {
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'ru.yandex.jenkins.plugins.debuilder.DebianPackagePublisher'
            children().size() == 3
            repoId[0].value() == 'debian-squeeze'
            commitMessage[0].value() == ''
            commitChanges[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('debian-package-builder', '1.6.7')
    }

    def 'debianPackage with empty commitMessage'() {
        when:
        context.debianPackage('debian-wheezy') {
            commitMessage(message)
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'ru.yandex.jenkins.plugins.debuilder.DebianPackagePublisher'
            children().size() == 3
            repoId[0].value() == 'debian-wheezy'
            commitMessage[0].value() == message
            commitChanges[0].value() == commit
        }
        1 * jobManagement.requireMinimumPluginVersion('debian-package-builder', '1.6.7')

        where:
        message                       | commit
        ''                            | false
        'automatic commit by Jenkins' | true
    }

    def 'call artifactDeployer with no options'() {
        when:
        context.artifactDeployer {
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.artifactdeployer.ArtifactDeployerPublisher'
            children().size() == 2
            deployEvenBuildFail[0].value() == false
            entries[0].value().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('artifactdeployer', '0.33')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'call artifactDeployer with all options'() {
        when:
        context.artifactDeployer {
            artifactsToDeploy {
                includes('test1')
                baseDir('test2')
                remoteFileLocation('test3')
                excludes('test4')
                flatten()
                cleanUp()
                deleteRemoteArtifacts()
                deleteRemoteArtifactsByScript('test5')
                failIfNoFiles()
            }
            artifactsToDeploy {}
            deployIfFailed()
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.artifactdeployer.ArtifactDeployerPublisher'
            children().size() == 2
            deployEvenBuildFail[0].value() == true
            with(entries[0]) {
                children().size() == 2
                with(children()[0]) {
                    name() == 'org.jenkinsci.plugins.artifactdeployer.ArtifactDeployerEntry'
                    children().size() == 10
                    includes[0].value() == 'test1'
                    basedir[0].value() == 'test2'
                    remote[0].value() == 'test3'
                    excludes[0].value() == 'test4'
                    flatten[0].value() == true
                    deleteRemote[0].value() == true
                    deleteRemoteArtifacts[0].value() == true
                    deleteRemoteArtifactsByScript[0].value() == true
                    groovyExpression[0].value() == 'test5'
                    failNoFilesDeploy[0].value() == true
                }
                with(children()[1]) {
                    name() == 'org.jenkinsci.plugins.artifactdeployer.ArtifactDeployerEntry'
                    children().size() == 9
                    includes[0].value().empty
                    basedir[0].value().empty
                    remote[0].value().empty
                    excludes[0].value().empty
                    flatten[0].value() == false
                    deleteRemote[0].value() == false
                    deleteRemoteArtifacts[0].value() == false
                    deleteRemoteArtifactsByScript[0].value() == false
                    failNoFilesDeploy[0].value() == false
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('artifactdeployer', '0.33')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'slocCount with no options'() {
        when:
        context.slocCount {}

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.sloccount.SloccountPublisher'
            children().size() == 5
            pattern[0].value().empty
            encoding[0].value().empty
            commentIsCode[0].value() == false
            ignoreBuildFailure[0].value() == false
            numBuildsInGraph[0].value() == 0
        }
        1 * jobManagement.requireMinimumPluginVersion('sloccount', '1.20')
    }

    def 'slocCount with all options'() {
        when:
        context.slocCount {
            pattern('build/result.xml')
            encoding('UTF-8')
            commentIsCode(value)
            buildsInGraph(10)
            ignoreBuildFailure(value)
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.sloccount.SloccountPublisher'
            children().size() == 5
            pattern[0].value() == 'build/result.xml'
            encoding[0].value() == 'UTF-8'
            numBuildsInGraph[0].value() == 10
            commentIsCode[0].value() == value
            ignoreBuildFailure[0].value() == value
        }
        1 * jobManagement.requireMinimumPluginVersion('sloccount', '1.20')

        where:
        value << [true, false]
    }

    def 'svnTag with no options'() {
        when:
        context.svnTag {}

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.svn__tag.SvnTagPublisher'
            children().size() == 3
            tagBaseURL[0].value() == "http://subversion_host/project/tags/last-successful/\${env['JOB_NAME']}"
            tagComment[0].value() == "Tagged by Jenkins svn-tag plugin. Build:\${env['BUILD_TAG']}."
            tagDeleteComment[0].value() == 'Delete old tag by svn-tag Jenkins plugin.'
        }
        1 * jobManagement.requireMinimumPluginVersion('svn-tag', '1.18')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'svnTag with all options'() {
        when:
        context.svnTag {
            baseUrl('http://subversion.com')
            comment('tag comment')
            deleteComment('delete comment')
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.svn__tag.SvnTagPublisher'
            children().size() == 3
            tagBaseURL[0].value() == 'http://subversion.com'
            tagComment[0].value() == 'tag comment'
            tagDeleteComment[0].value() == 'delete comment'
        }
        1 * jobManagement.requireMinimumPluginVersion('svn-tag', '1.18')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'call cucumberReports with no options'() {
        when:
        context.cucumberReports {
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'net.masterthought.jenkins.CucumberReportPublisher'
            children().size() == 11
            jsonReportDirectory[0].value().empty
            pluginUrlPath[0].value().empty
            fileIncludePattern[0].value().empty
            fileExcludePattern[0].value().empty
            skippedFails[0].value() == false
            pendingFails[0].value() == false
            undefinedFails[0].value() == false
            missingFails[0].value() == false
            noFlashCharts[0].value() == false
            ignoreFailedTests[0].value() == false
            parallelTesting[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('cucumber-reports', '0.6.0')
    }

    def 'call cucumberReports with all options'() {
        when:
        context.cucumberReports {
            jsonReportPath('files.json')
            pluginUrlPath('url')
            fileIncludePattern('included')
            fileExcludePattern('excluded')
            failOnSkipSteps(value)
            failOnPendingSteps(value)
            failOnUndefinedSteps(value)
            failOnMissingSteps(value)
            turnOffFlashCharts(value)
            ignoreFailedTests(value)
            parallelTesting(value)
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'net.masterthought.jenkins.CucumberReportPublisher'
            children().size() == 11
            jsonReportDirectory[0].value() == 'files.json'
            pluginUrlPath[0].value() == 'url'
            fileIncludePattern[0].value() == 'included'
            fileExcludePattern[0].value() == 'excluded'
            skippedFails[0].value() == value
            pendingFails[0].value() == value
            undefinedFails[0].value() == value
            missingFails[0].value() == value
            noFlashCharts[0].value() == value
            ignoreFailedTests[0].value() == value
            parallelTesting[0].value() == value
        }
        1 * jobManagement.requireMinimumPluginVersion('cucumber-reports', '0.6.0')

        where:
        value << [true, false]
    }

    def 'call cucumberTestResults with no options'() {
        when:
        context.cucumberTestResults {
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.cucumber.jsontestsupport.CucumberTestResultArchiver'
            children().size() == 2
            testResults[0].value().empty
            ignoreBadSteps[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('cucumber-testresult-plugin', '0.8.2')
    }

    def 'call cucumberTestResults with all options'() {
        when:
        context.cucumberTestResults {
            jsonReportFiles('file.json')
            ignoreBadSteps(value)
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.cucumber.jsontestsupport.CucumberTestResultArchiver'
            children().size() == 2
            testResults[0].value() == 'file.json'
            ignoreBadSteps[0].value() == value
        }
        1 * jobManagement.requireMinimumPluginVersion('cucumber-testresult-plugin', '0.8.2')

        where:
        value << [true, false]
    }

    def 'call mantis with no options'() {
        when:
        context.mantis {
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.mantis.MantisIssueUpdater'
            children().size() == 2
            keepNotePrivate[0].value() == false
            recordChangelog[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('mantis', '0.26')
    }

    def 'call mantis with all options'() {
        when:
        context.mantis {
            keepNotePrivate(value)
            recordChangelogToNote(value)
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.mantis.MantisIssueUpdater'
            children().size() == 2
            keepNotePrivate[0].value() == value
            recordChangelog[0].value() == value
        }
        1 * jobManagement.requireMinimumPluginVersion('mantis', '0.26')

        where:
        value << [true, false]
    }

    def 'publish deployment to WebLogic with least args'() {
        when:
        context.deployToWeblogic {}

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.deploy.weblogic.WeblogicDeploymentPlugin'
            children().size() == 6
            mustExitOnFailure[0].value() == false
            forceStopOnFirstFailure[0].value() == false
            selectedDeploymentStrategyIds[0].value().empty
            isDeployingOnlyWhenUpdates[0].value() == false
            deployedProjectsDependencies[0].value().empty
            tasks[0].value().empty
        }
    }

    def 'publish one deployment to WebLogic with all args'() {
        when:
        context.deployToWeblogic {
            mustExitOnFailure()
            forceStopOnFirstFailure()
            deployingOnlyWhenUpdates()
            deployedProjectsDependencies('abc,def')
            deploymentPolicies {
                legacyCode()
                user()
                userId()
                remoteHost()
                upstream()
                deploymentTimer()
                scmChange()
            }
            task {
                weblogicEnvironmentTargetedName('test_environment')
                deploymentName('myApp')
                deploymentTargets('ms_one')
                isLibrary()
                builtResourceRegexToDeploy('myApp.ear')
                baseResourcesGeneratedDirectory('foo')
                taskName('test_deploy_task')
                jdkName('JDK 7')
                jdkHome('lala')
                stageMode(stageModeEnum)
                commandLine('one;')
                commandLine('two;')
                deploymentPlan('test')
            }
            task {}
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.deploy.weblogic.WeblogicDeploymentPlugin'
            children().size() == 6
            mustExitOnFailure[0].value() == true
            forceStopOnFirstFailure[0].value() == true
            isDeployingOnlyWhenUpdates[0].value() == true
            deployedProjectsDependencies[0].value() == 'abc,def'
            with(selectedDeploymentStrategyIds[0]) {
                children().size() == 7
                string[0].value() == 'hudson.model.Cause\\\\$LegacyCodeCause'
                string[1].value() == 'hudson.model.Cause\\\\$UserCause'
                string[2].value() == 'hudson.model.Cause\\\\$UserIdCause'
                string[3].value() == 'hudson.model.Cause\\\\$RemoteCause'
                string[4].value() == 'hudson.model.Cause\\\\$UpstreamCause'
                string[5].value() ==
                        'org.jenkinsci.plugins.deploy.weblogic.trigger.DeploymentTrigger\\\\$DeploymentTriggerCause'
                string[6].value() == 'hudson.triggers.SCMTrigger\\\\$SCMTriggerCause'
            }
            tasks[0].children().size() == 2
            with(tasks[0].children()[0]) {
                name() == 'org.jenkinsci.plugins.deploy.weblogic.data.DeploymentTask'
                children().size() == 12
                !id[0].value().empty
                weblogicEnvironmentTargetedName[0].value() == 'test_environment'
                deploymentName[0].value() == 'myApp'
                deploymentTargets[0].value() == 'ms_one'
                isLibrary[0].value() == true
                builtResourceRegexToDeploy[0].value() == 'myApp.ear'
                baseResourcesGeneratedDirectory[0].value() == 'foo'
                taskName[0].value() == 'test_deploy_task'
                jdk[0].children().size() == 2
                jdk[0].name[0].value() == 'JDK 7'
                jdk[0].home[0].value() == 'lala'
                stageMode[0].value() == stageModeString
                commandLine[0].value() == 'one;\ntwo;'
                deploymentPlan[0].value() == 'test'
            }
            with(tasks[0].children()[1]) {
                name() == 'org.jenkinsci.plugins.deploy.weblogic.data.DeploymentTask'
                children().size() == 12
                !id[0].value().empty
                weblogicEnvironmentTargetedName[0].value() == ''
                deploymentName[0].value() == ''
                deploymentTargets[0].value() == 'AdminServer'
                isLibrary[0].value() == false
                builtResourceRegexToDeploy[0].value() == ''
                baseResourcesGeneratedDirectory[0].value() == ''
                taskName[0].value() == ''
                jdk[0].children().size() == 2
                jdk[0].name[0].value() == ''
                jdk[0].home[0].value() == ''
                stageMode[0].value() == 'bydefault'
                commandLine[0].value() == ''
                deploymentPlan[0].value() == ''
            }
        }

        where:
        stageModeEnum                               || stageModeString
        WeblogicDeploymentStageModes.BY_DEFAULT     || 'bydefault'
        WeblogicDeploymentStageModes.EXTERNAL_STAGE || 'external_stage'
        WeblogicDeploymentStageModes.NO_STAGE       || 'nostage'
        WeblogicDeploymentStageModes.STAGE          || 'stage'
    }

    def 'call flog with no options'() {
        when:
        context.flog()

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.rubyMetrics.flog.FlogPublisher'
            children().size() == 2
            rbDirectories[0].value().empty
            with(splittedDirectories[0]) {
                children().size() == 1
                string[0].value() == '.'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('rubyMetrics', '1.6.3')
    }

    def 'call flog with all options'() {
        when:
        context.flog {
            rubyDirectories('a', 'b')
            rubyDirectories('c')
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.rubyMetrics.flog.FlogPublisher'
            children().size() == 2
            rbDirectories[0].value() == 'a\nb\nc'
            with(splittedDirectories[0]) {
                children().size() == 3
                string[0].value() == 'a'
                string[1].value() == 'b'
                string[2].value() == 'c'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('rubyMetrics', '1.6.3')
    }

    def 'call railsNotes with no options'() {
        when:
        context.railsNotes()

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.rubyMetrics.railsNotes.RailsNotesPublisher'
            children().size() == 4
            with(rake[0]) {
                name() == 'rake'
                children().size() == 5
                rakeInstallation[0].value() == '(Default)'
                rakeWorkingDir[0].value().empty
                tasks[0].value() == 'notes'
                silent[0].value() == true
                bundleExec[0].value() == true
            }
            rakeInstallation[0].value() == '(Default)'
            rakeWorkingDir[0].value().empty
            task[0].value() == 'notes'
        }
        1 * jobManagement.requireMinimumPluginVersion('rubyMetrics', '1.6.3')
    }

    def 'call railsNotes with all options'() {
        when:
        context.railsNotes {
            rakeVersion('rake_2')
            rakeWorkingDirectory('src')
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.rubyMetrics.railsNotes.RailsNotesPublisher'
            children().size() == 4
            with(rake[0]) {
                name() == 'rake'
                children().size() == 5
                rakeInstallation[0].value() == 'rake_2'
                rakeWorkingDir[0].value() == 'src'
                tasks[0].value() == 'notes'
                silent[0].value() == true
                bundleExec[0].value() == true
            }
            rakeInstallation[0].value() == 'rake_2'
            rakeWorkingDir[0].value() == 'src'
            task[0].value() == 'notes'
        }
        1 * jobManagement.requireMinimumPluginVersion('rubyMetrics', '1.6.3')
    }

    def 'call railsStats with no options'() {
        when:
        context.railsStats {
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.rubyMetrics.railsStats.RailsStatsPublisher'
            children().size() == 4
            with(rake[0]) {
                name() == 'rake'
                children().size() == 5
                rakeInstallation[0].value() == '(Default)'
                rakeWorkingDir[0].value().empty
                tasks[0].value() == 'stats'
                silent[0].value() == true
                bundleExec[0].value() == true
            }
            rakeInstallation[0].value() == '(Default)'
            rakeWorkingDir[0].value().empty
            task[0].value() == 'stats'
        }
        1 * jobManagement.requireMinimumPluginVersion('rubyMetrics', '1.6.3')
    }

    def 'call railsStats with all options'() {
        when:
        context.railsStats {
            rakeVersion('rake_2')
            rakeWorkingDirectory('src')
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.rubyMetrics.railsStats.RailsStatsPublisher'
            children().size() == 4
            with(rake[0]) {
                name() == 'rake'
                children().size() == 5
                rakeInstallation[0].value() == 'rake_2'
                rakeWorkingDir[0].value() == 'src'
                tasks[0].value() == 'stats'
                silent[0].value() == true
                bundleExec[0].value() == true
            }
            rakeInstallation[0].value() == 'rake_2'
            rakeWorkingDir[0].value() == 'src'
            task[0].value() == 'stats'
        }

        1 * jobManagement.requireMinimumPluginVersion('rubyMetrics', '1.6.3')
    }

    def 'call seleniumReport with no options'() {
        when:
        context.seleniumReport()

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.seleniumhq.SeleniumhqPublisher'
            children().size() == 2
            testResults[0].value().empty
            useTestCommands[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('seleniumhq', '0.4')
    }

    def 'call seleniumReport with all options'() {
        when:
        context.seleniumReport('./selenium*') {
            useTestCommands(value)
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.seleniumhq.SeleniumhqPublisher'
            children().size() == 2
            testResults[0].value() == './selenium*'
            useTestCommands[0].value() == value
        }
        1 * jobManagement.requireMinimumPluginVersion('seleniumhq', '0.4')

        where:
        value << [true, false]
    }

    def 'call seleniumHtmlReport with no options'() {
        when:
        context.seleniumHtmlReport()

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.seleniumhtmlreport.SeleniumHtmlReportPublisher'
            children().size() == 3
            testResultsDir[0].value() == 'target'
            failureIfExceptionOnParsingResultFiles[0].value() == true
            SELENIUM__REPORTS__TARGET[0].value() == 'seleniumReports'
        }
        1 * jobManagement.requireMinimumPluginVersion('seleniumhtmlreport', '1.0')
    }

    def 'call seleniumHtmlReport with all options'() {
        when:
        context.seleniumHtmlReport('./selenium') {
            failOnExceptions(value)
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.seleniumhtmlreport.SeleniumHtmlReportPublisher'
            children().size() == 3
            testResultsDir[0].value() == './selenium'
            failureIfExceptionOnParsingResultFiles[0].value() == value
            SELENIUM__REPORTS__TARGET[0].value() == 'seleniumReports'
        }
        1 * jobManagement.requireMinimumPluginVersion('seleniumhtmlreport', '1.0')

        where:
        value << [true, false]
    }

    def 'call rcov with no options'() {
        when:
        context.rcov {
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.rubyMetrics.rcov.RcovPublisher'
            children().size() == 2
            reportDir[0].value().empty
            with(targets[0]) {
                children().size() == 2
                with(children()[0]) {
                    name() == 'hudson.plugins.rubyMetrics.rcov.model.MetricTarget'
                    children().size() == 4
                    metric[0].value() == 'TOTAL_COVERAGE'
                    healthy[0].value() == 80
                    unhealthy[0].value() == 0
                    unstable[0].value() == 0
                }
                with(children()[1]) {
                    name() == 'hudson.plugins.rubyMetrics.rcov.model.MetricTarget'
                    children().size() == 4
                    metric[0].value() == 'CODE_COVERAGE'
                    healthy[0].value() == 80
                    unhealthy[0].value() == 0
                    unstable[0].value() == 0
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('rubyMetrics', '1.6.3')
    }

    def 'call rcov with all options'() {
        when:
        context.rcov {
            reportDirectory('folder')
            totalCoverage(90, 50, 10)
            codeCoverage(91, 51, 11)
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.rubyMetrics.rcov.RcovPublisher'
            children().size() == 2
            reportDir[0].value() == 'folder'
            with(targets[0]) {
                children().size() == 2
                with(children()[0]) {
                    name() == 'hudson.plugins.rubyMetrics.rcov.model.MetricTarget'
                    children().size() == 4
                    metric[0].value() == 'TOTAL_COVERAGE'
                    healthy[0].value() == 90
                    unhealthy[0].value() == 50
                    unstable[0].value() == 10
                }
                with(children()[1]) {
                    name() == 'hudson.plugins.rubyMetrics.rcov.model.MetricTarget'
                    children().size() == 4
                    metric[0].value() == 'CODE_COVERAGE'
                    healthy[0].value() == 91
                    unhealthy[0].value() == 51
                    unstable[0].value() == 11
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('rubyMetrics', '1.6.3')
    }

    def 'call cloverPHP with missing XML location'() {
        when:
        context.cloverPHP(xmlLocation)

        then:
        thrown(DslScriptException)

        where:
        xmlLocation << [null, '']
    }

    def 'call cloverPHP with missing report directory'() {
        when:
        context.cloverPHP('foo') {
            publishHtmlReport(directory)
        }

        then:
        thrown(DslScriptException)

        where:
        directory << [null, '']
    }

    def 'call cloverPHP minimal options'() {
        when:
        context.cloverPHP('html')

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.cloverphp.CloverPHPPublisher'
            children().size() == 6
            publishHtmlReport[0].value() == false
            xmlLocation[0].value() == 'html'
            disableArchiving[0].value() == false
            with(healthyTarget[0]) {
                children().size() == 2
                methodCoverage[0].value() == 70
                statementCoverage[0].value() == 80
            }
            unhealthyTarget[0].value().empty
            failingTarget[0].value().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('cloverphp', '0.5')
    }

    def 'call cloverPHP with all options'() {
        when:
        context.cloverPHP('html') {
            publishHtmlReport('dir') {
                disableArchiving(disable)
            }
            healthyMethodCoverage(null)
            healthyStatementCoverage(null)
            unhealthyMethodCoverage(3)
            unhealthyStatementCoverage(4)
            unstableMethodCoverage(5)
            unstableStatementCoverage(6)
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'org.jenkinsci.plugins.cloverphp.CloverPHPPublisher'
            children().size() == 7
            publishHtmlReport[0].value() == true
            reportDir[0].value() == 'dir'
            xmlLocation[0].value() == 'html'
            disableArchiving[0].value() == disable
            healthyTarget[0].value().empty
            with(unhealthyTarget[0]) {
                children().size() == 2
                methodCoverage[0].value() == 3
                statementCoverage[0].value() == 4
            }
            with(failingTarget[0]) {
                children().size() == 2
                methodCoverage[0].value() == 5
                statementCoverage[0].value() == 6
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('cloverphp', '0.5')

        where:
        disable << [true, false]
    }

    def 'call emotional with no options'() {
        when:
        context.emotional()

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'org.jenkinsci.plugins.emotional__jenkins.EmotionalJenkinsPublisher'
        1 * jobManagement.requireMinimumPluginVersion('emotional-jenkins-plugin', '1.2')
    }

    def 'call jiraIssueUpdater'() {
        when:
        context.jiraIssueUpdater()

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.jira.JiraIssueUpdater'
        1 * jobManagement.requireMinimumPluginVersion('jira', '1.39')
    }

    def 'call releaseJiraVersion with no options'() {
        when:
        context.releaseJiraVersion {
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jira.JiraReleaseVersionUpdater'
            children().size() == 2
            jiraProjectKey[0].value().empty
            jiraRelease[0].value().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('jira', '1.39')
    }

    def 'call releaseJiraVersion with all options'() {
        when:
        context.releaseJiraVersion {
            delegate.projectKey(projectKey)
            delegate.release(release)
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jira.JiraReleaseVersionUpdater'
            children().size() == 2
            jiraProjectKey[0].value() == expectedKey
            jiraRelease[0].value() == expectedRelease
        }
        1 * jobManagement.requireMinimumPluginVersion('jira', '1.39')

        where:
        projectKey | release | expectedKey | expectedRelease
        null       | null    | ''          | ''
        'key'      | null    | 'key'       | ''
        null       | 'key2'  | ''          | 'key2'
        'key1'     | 'key2'  | 'key1'      | 'key2'
    }

    def 'call moveJiraIssues with no options'() {
        when:
        context.moveJiraIssues {
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jira.JiraIssueMigrator'
            children().size() == 4
            jiraProjectKey[0].value().empty
            jiraRelease[0].value().empty
            jiraReplaceVersion[0].value().empty
            jiraQuery[0].value().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('jira', '1.39')
    }

    def 'call moveJiraIssues with all options'() {
        when:
        context.moveJiraIssues {
            delegate.projectKey(projectKey)
            delegate.release(release)
            replaceVersion(replace)
            delegate.query(query)
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jira.JiraIssueMigrator'
            children().size() == 4
            jiraProjectKey[0].value() == expectedKey
            jiraRelease[0].value() == expectedRelease
            jiraReplaceVersion[0].value() == expectedReplace
            jiraQuery[0].value() == expectedQuery
        }
        1 * jobManagement.requireMinimumPluginVersion('jira', '1.39')

        where:
        projectKey | release | replace | query | expectedKey | expectedRelease | expectedReplace | expectedQuery
        null       | null    | null    | null  | ''          | ''              | ''              | ''
        'key'      | null    | null    | null  | 'key'       | ''              | ''              | ''
        'key1'     | 'key2'  | null    | null  | 'key1'      | 'key2'          | ''              | ''
        'key1'     | 'key2'  | 'key3'  | null  | 'key1'      | 'key2'          | 'key3'          | ''
        'key1'     | 'key2'  | 'key3'  | 'key' | 'key1'      | 'key2'          | 'key3'          | 'key'
    }

    def 'call createJiraVersion with no options'() {
        when:
        context.createJiraVersion {
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jira.JiraVersionCreator'
            children().size() == 2
            jiraProjectKey[0].value().empty
            jiraVersion[0].value().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('jira', '1.39')
    }

    def 'call createJiraVersion with all options'() {
        when:
        context.createJiraVersion {
            delegate.projectKey(projectKey)
            delegate.version(version)
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jira.JiraVersionCreator'
            children().size() == 2
            jiraProjectKey[0].value() == expectedKey
            jiraVersion[0].value() == expectedVersion
        }
        1 * jobManagement.requireMinimumPluginVersion('jira', '1.39')

        where:
        projectKey | version | expectedKey | expectedVersion
        null       | null    | ''          | ''
        'key'      | null    | 'key'       | ''
        null       | 'key2'  | ''          | 'key2'
        'key1'     | 'key2'  | 'key1'      | 'key2'
    }

    def 'call createJiraIssue with no options'() {
        when:
        context.createJiraIssue {
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jira.JiraCreateIssueNotifier'
            children().size() == 4
            projectKey[0].value().empty
            testDescription[0].value().empty
            assignee[0].value().empty
            component[0].value().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('jira', '1.39')
    }

    def 'call createJiraIssue with all options'() {
        when:
        context.createJiraIssue {
            projectKey(key)
            testDescription(desc)
            assignee(assig)
            component(comp)
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.jira.JiraCreateIssueNotifier'
            children().size() == 4
            projectKey[0].value() == expectedKey
            testDescription[0].value() == expectedDesc
            assignee[0].value() == expectedAssignee
            component[0].value() == expectedComponent
        }
        1 * jobManagement.requireMinimumPluginVersion('jira', '1.39')

        where:
        key    | desc   | assig  | comp  | expectedKey | expectedDesc | expectedAssignee | expectedComponent
        null   | null   | null   | null  | ''          | ''           | ''               | ''
        'key'  | null   | null   | null  | 'key'       | ''           | ''               | ''
        'key1' | 'key2' | null   | null  | 'key1'      | 'key2'       | ''               | ''
        'key1' | 'key2' | 'key3' | null  | 'key1'      | 'key2'       | 'key3'           | ''
        'key1' | 'key2' | 'key3' | 'key' | 'key1'      | 'key2'       | 'key3'           | 'key'
    }

    def 'call consoleParsing without rules'() {
        when:
        context.consoleParsing {
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'No rule path specified'
    }

    def 'call consoleParsing with all rules'() {
        when:
        context.consoleParsing {
            globalRules('/locations')
            projectRules('/locations')
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'Only one rule path must be specified'
    }

    def 'call consoleParsing with projectRules'() {
        when:
        context.consoleParsing {
            unstableOnWarning()
            failBuildOnError()
            showGraphs()
            projectRules('/locations')
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.logparser.LogParserPublisher'
            children().size() == 5
            unstableOnWarning[0].value() == true
            failBuildOnError[0].value() == true
            showGraphs[0].value() == true
            useProjectRule[0].value() == true
            projectRulePath[0].value() == '/locations'
        }
        1 * jobManagement.requireMinimumPluginVersion('log-parser', '2.0')
    }

    def 'call consoleParsing with globalRules'() {
        when:
        context.consoleParsing {
            globalRules('/locations')
        }

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            name() == 'hudson.plugins.logparser.LogParserPublisher'
            children().size() == 5
            unstableOnWarning[0].value() == false
            failBuildOnError[0].value() == false
            showGraphs[0].value() == false
            useProjectRule[0].value() == false
            parsingRulesPath[0].value() == '/locations'
        }

        1 * jobManagement.requireMinimumPluginVersion('log-parser', '2.0')
    }
}
