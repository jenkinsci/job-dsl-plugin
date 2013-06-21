package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
//import groovy.xml.XmlUtil
//import hudson.util.Secret
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

import static javaposse.jobdsl.dsl.helpers.UrlTriggerEntryContext.Check.etag
import static javaposse.jobdsl.dsl.helpers.UrlTriggerEntryContext.Check.lastModified
import static javaposse.jobdsl.dsl.helpers.UrlTriggerEntryContext.Check.status
import static javaposse.jobdsl.dsl.helpers.UrlTriggerInspectionContext.Inspection.change

/**
 triggers {scm(String cronString)
 cron(String cronString)}*/
class TriggerContextHelper extends AbstractContextHelper<TriggerContext> {

    TriggerContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    /**
     * Public method available on job {}* @param closure
     * @return
     */
    def triggers(Closure closure) {
        execute(closure, new TriggerContext(withXmlActions, type, []))
    }

    Closure generateWithXmlClosure(TriggerContext context) {
        return { Node project ->
            def triggersNode
            if (project.triggers.isEmpty()) {
                triggersNode = project.appendNode('triggers', [class: 'vector'])
            } else {
                triggersNode = project.triggers[0]
            }
            context.triggerNodes.each {
                triggersNode << it
            }
        }
    }
}

class GerritEventContext implements Context {
    def eventShortNames = []

    def propertyMissing(String shortName) {
        eventShortNames << shortName
    }
}

class GerritSpec {
    GerritSpec(String raw) {
        def idx = raw.indexOf(':')
        def prefix = (idx == -1)?'':raw.substring(0, idx).toUpperCase()
        if (availableTypes.contains(prefix)) {
            type = prefix
            pattern = raw.substring(idx + 1)
        } else {
            type = 'PLAIN'
            pattern = raw
        }
    }

    def availableTypes = ['ANT', 'PLAIN', 'REG_EXP']
    String type
    String pattern
}

class GerritContext implements Context {
    GerritEventContext eventContext = new GerritEventContext()
    Closure configureClosure
    def projects = []

    def configure(Closure configureClosure) {
        // save for later
        this.configureClosure = configureClosure
    }

    def events(Closure eventClosure) {
        AbstractContextHelper.executeInContext(eventClosure, eventContext)
    }

    def project(String projectName, List<String> branches) {
        projects << [new GerritSpec(projectName), branches.collect { new GerritSpec(it) }]
    }

    def project(String projectName, String branch) {
        project(projectName, [branch])
    }
}

/**
 <inspectingContent>true</inspectingContent>
 <contentTypes>
 <org.jenkinsci.plugins.urltrigger.content.JSONContentType>
 <jsonPaths>
 <org.jenkinsci.plugins.urltrigger.content.JSONContentEntry>
 <jsonPath>/foo/bar</jsonPath>
 </org.jenkinsci.plugins.urltrigger.content.JSONContentEntry>
 <org.jenkinsci.plugins.urltrigger.content.JSONContentEntry>
 <jsonPath>/foo/</jsonPath>
 </org.jenkinsci.plugins.urltrigger.content.JSONContentEntry>
 </jsonPaths>
 </org.jenkinsci.plugins.urltrigger.content.JSONContentType>
 <org.jenkinsci.plugins.urltrigger.content.TEXTContentType>
 <regExElements>
 <org.jenkinsci.plugins.urltrigger.content.TEXTContentEntry>
 <regEx>.*</regEx>
 </org.jenkinsci.plugins.urltrigger.content.TEXTContentEntry>
 </regExElements>
 </org.jenkinsci.plugins.urltrigger.content.TEXTContentType>
 </contentTypes>
 */
class UrlTriggerInspectionContext implements Context {

    enum Inspection {
        change('org.jenkinsci.plugins.urltrigger.content.SimpleContentType', null, null, null),
        json('org.jenkinsci.plugins.urltrigger.content.JSONContentType', 'jsonPaths', 'org.jenkinsci.plugins.urltrigger.content.JSONContentEntry', 'jsonPath'),
        text('org.jenkinsci.plugins.urltrigger.content.TEXTContentType', 'regExElements', 'org.jenkinsci.plugins.urltrigger.content.TEXTContentEntry', 'regEx'),
        xml('org.jenkinsci.plugins.urltrigger.content.XMLContentType', 'xPaths', 'org.jenkinsci.plugins.urltrigger.content.XMLContentEntry', 'xPath')

        final String node
        final String list
        final String entry
        final String path

        Inspection(String node, String list, String entry, String path) {
            this.node = node
            this.list = list
            this.entry = entry
            this.path = path
        }
    }

    Inspection type
    def expressions = []

    UrlTriggerInspectionContext(Inspection type) {
        this.type = Preconditions.checkNotNull(type, "Inspection type must not be null!")
    }

    def path(String path) {
        String p = Preconditions.checkNotNull(path, "Path must not be null")
        Preconditions.checkArgument(!p.empty, "Path given must not be empty")
        expressions << p
    }

    def regexp(String exp) {
        def expr = Preconditions.checkNotNull(exp, "Regular expression must not be null")
        Preconditions.checkArgument(!expr.empty, "Regular expressions must not be empty")
        try {
            Pattern.compile(expr)
        } catch (PatternSyntaxException pse) {
            throw new IllegalArgumentException("Syntax of pattern ${exp} is invalid: ${pse.message}")
        }

        expressions << exp
    }

}

/**
 <org.jenkinsci.plugins.urltrigger.URLTriggerEntry>
 <url>http://source-1.search.dev.fra1.xing.com/content/repositories/snapshots/com/xing/dia/tagindex-common/maven-metadata.xml</url>
 <proxyActivated>false</proxyActivated>
 <checkStatus>false</checkStatus>
 <statusCode>200</statusCode>
 <timeout>300</timeout>
 <checkETag>false</checkETag>
 <checkLastModificationDate>false</checkLastModificationDate>
 <inspectingContent>false</inspectingContent>
 <contentTypes/>
 </org.jenkinsci.plugins.urltrigger.URLTriggerEntry>
 */
class UrlTriggerEntryContext implements Context {

    enum Check {
        status,
        etag,
        lastModified
    }

    def username
    def password

    def url
    def statusCode = 200
    def timeout = 300
    def proxyActivated = false
    EnumSet<Check> checks = EnumSet.noneOf(Check)
    def inspections = []

    UrlTriggerEntryContext(String url) {
        this.url = Preconditions.checkNotNull(url, "The URL is required for urlTrigger()")
        Preconditions.checkArgument(url != "", "URL must not be empty.")
        this.statusCode = statusCode
        this.timeout = timeout
    }

    def proxy(boolean active) {
        this.proxyActivated = active
    }

    def status(int statusCode) {
        this.statusCode = statusCode
    }

    def timeout(long timeout) {
        this.timeout = timeout
    }

    def check(String performCheck) {
        Check check = Preconditions.checkNotNull(Check.valueOf(performCheck), "Check must be one of: ${Check.values()}" as Object)
        checks << check
    }

    def inspection(String type, Closure inspectionClosure = null) {
        UrlTriggerInspectionContext.Inspection itype = Preconditions.checkNotNull(
                UrlTriggerInspectionContext.Inspection.valueOf(type),
                "Inspection must be one of ${UrlTriggerInspectionContext.Inspection.values()}" as Object)

        UrlTriggerInspectionContext inspection = new UrlTriggerInspectionContext(itype)
        AbstractContextHelper.executeInContext(inspectionClosure, inspection)

        inspections << inspection
    }

    /* *
     * Basic Authentication currently unsupported, because this requires the encoding of the password
     * with the Jenkins instance specific key that is only cleanly accessible via the shared Jenkins runtime instance.
     */
    /*def basicAuth(String username, String password) {
        this.username = Preconditions.checkNotNull(username, "Username is required for authentication")
        this.password = new Secret(password).encryptedValue
    }*/

}

class UrlTriggerContext implements Context {
    Closure configureClosure
    def label
    def entries = []
    String crontab = 'H/5 * * * *'

    UrlTriggerContext(String cron = null) {
        if (cron) this.crontab = cron
    }

    def configure(Closure configureClosure) {
        this.configureClosure = configureClosure
    }

    def restrictToLabel(String label) {
        this.label = label
    }

    def cron(String cron) {
        this.crontab = cron
    }

    def url(String url, Closure entryClosure = null) {
        UrlTriggerEntryContext entryContext = new UrlTriggerEntryContext(url)
        AbstractContextHelper.executeInContext(entryClosure, entryContext)
        entries << entryContext
    }

}

class TriggerContext implements Context {
    List<WithXmlAction> withXmlActions
    JobType jobType
    List<Node> triggerNodes

    TriggerContext(List<WithXmlAction> withXmlActions = [], JobType jobType = JobType.Freeform, List<Node> triggerNodes = []) {
        this.withXmlActions = withXmlActions
        this.jobType = jobType
        this.triggerNodes = triggerNodes
    }

    /**
     <org.jenkinsci.plugins.urltrigger.URLTrigger plugin="urltrigger@0.31">
     <spec>H/5 * * * *</spec>
     <entries>
     <org.jenkinsci.plugins.urltrigger.URLTriggerEntry>
     <url>http://source-1.search.dev.fra1.xing.com/content/repositories/snapshots/com/xing/dia/recommenders-common/maven-metadata.xml</url>
     <proxyActivated>false</proxyActivated>
     <checkStatus>false</checkStatus>
     <statusCode>200</statusCode>
     <timeout>300</timeout>
     <checkETag>false</checkETag>
     <checkLastModificationDate>false</checkLastModificationDate>
     <inspectingContent>false</inspectingContent>
     <contentTypes/>
     </org.jenkinsci.plugins.urltrigger.URLTriggerEntry>
     <org.jenkinsci.plugins.urltrigger.URLTriggerEntry>
     <url>http://source-1.search.dev.fra1.xing.com/content/repositories/snapshots/com/xing/ds/ds-services/maven-metadata.xml</url>
     <proxyActivated>false</proxyActivated>
     <checkStatus>false</checkStatus>
     <statusCode>200</statusCode>
     <timeout>300</timeout>
     <checkETag>false</checkETag>
     <checkLastModificationDate>false</checkLastModificationDate>
     <inspectingContent>false</inspectingContent>
     <contentTypes/>
     </org.jenkinsci.plugins.urltrigger.URLTriggerEntry>
     <org.jenkinsci.plugins.urltrigger.URLTriggerEntry>
     <url>http://source-1.search.dev.fra1.xing.com/content/repositories/snapshots/com/xing/dia/tagindex-common/maven-metadata.xml</url>
     <proxyActivated>false</proxyActivated>
     <checkStatus>false</checkStatus>
     <statusCode>200</statusCode>
     <timeout>300</timeout>
     <checkETag>false</checkETag>
     <checkLastModificationDate>false</checkLastModificationDate>
     <inspectingContent>false</inspectingContent>
     <contentTypes/>
     </org.jenkinsci.plugins.urltrigger.URLTriggerEntry>
     </entries>
     <labelRestriction>false</labelRestriction>
     </org.jenkinsci.plugins.urltrigger.URLTrigger>
     </triggers>
     */
    def urlTrigger(Closure contextClosure) {
        urlTrigger(null, contextClosure)
    }

    def urlTrigger(String crontab, Closure contextClosure) {

        UrlTriggerContext urlTriggerContext = new UrlTriggerContext(crontab)
        AbstractContextHelper.executeInContext(contextClosure, urlTriggerContext)

        def nodeBuilder = new NodeBuilder()
        def urlTriggerNode = nodeBuilder.'org.jenkinsci.plugins.urltrigger.URLTrigger'(plugin: 'urltrigger@0.31') {
            spec urlTriggerContext.crontab
            if (urlTriggerContext.label) {
                labelRestriction true
                triggerLabel urlTriggerContext.label
            } else {
                labelRestriction false
            }
            if (urlTriggerContext.entries) {
                entries {
                    urlTriggerContext.entries.each { entry ->
                        'org.jenkinsci.plugins.urltrigger.URLTriggerEntry' {
                            url entry.url
                            statusCode entry.statusCode
                            timeout entry.timeout
                            proxyActivated entry.proxyActivated

                            /* Does not work right now due to dependencies on Jenkins for encryption */
                            /*if (entry.username && entry.password) {
                                username entry.username
                                password entry.password
                            }*/

                            checkStatus entry.checks.contains(status)
                            checkETag entry.checks.contains(etag)
                            checkLastModificationDate entry.checks.contains(lastModified)

                            if ((!entry.inspections.empty)) {
                                inspectingContent true
                                contentTypes {
                                    entry.inspections.each { insp ->
                                        "${insp.type.node}" {
                                            if (insp.type != change) {
                                                "${insp.type.list}" {
                                                    insp.expressions.each{ p ->
                                                        "${insp.type.entry}" {
                                                            "${insp.type.path}"(p)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }

                }
            }
        }

        //println XmlUtil.serialize(urlTriggerNode)

        // Apply their overrides
        if (urlTriggerContext.configureClosure) {
            WithXmlAction action = new WithXmlAction(urlTriggerContext.configureClosure)
            action.execute(urlTriggerNode)
        }

        triggerNodes << urlTriggerNode
    }

    def cron(String cronString) {
        Preconditions.checkNotNull(cronString)
        triggerNodes << new NodeBuilder().'hudson.triggers.TimerTrigger' {
            spec cronString
        }
    }

    /**
     <triggers class="vector">
     <hudson.triggers.SCMTrigger>
     <spec>10 * * * *</spec>
     </hudson.triggers.SCMTrigger>
     </triggers>
     */
    def scm(String cronString) {
        Preconditions.checkNotNull(cronString)
        triggerNodes << new NodeBuilder().'hudson.triggers.SCMTrigger' {
            spec cronString
        }
    }

    /**
     <com.cloudbees.jenkins.GitHubPushTrigger plugin="github@1.6">
     <spec></spec>
     </com.cloudbees.jenkins.GitHubPushTrigger>
     */
    def githubPush() {
        def attributes = [plugin: 'github@1.6']
        triggerNodes << new NodeBuilder().'com.cloudbees.jenkins.GitHubPushTrigger'(attributes) {
            spec ''
        }
    }

    /**
     <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger>
     <spec></spec>
     <gerritProjects>
     <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject>
     <compareType>PLAIN</compareType>
     <pattern>test-project</pattern>
     <branches>
     <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.Branch>
     <compareType>ANT</compareType>
     <pattern>**</pattern>
     </com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.Branch>
     </branches>
     </com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject>
     </gerritProjects>
     <silentMode>false</silentMode>
     <escapeQuotes>true</escapeQuotes>
     <buildStartMessage></buildStartMessage>
     <buildFailureMessage></buildFailureMessage>
     <buildSuccessfulMessage></buildSuccessfulMessage>
     <buildUnstableMessage></buildUnstableMessage>
     <buildNotBuiltMessage></buildNotBuiltMessage>
     <buildUnsuccessfulFilepath></buildUnsuccessfulFilepath>
     <customUrl></customUrl>
     <triggerOnEvents>
     <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.PluginChangeMergedEvent/>
     <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.PluginPatchsetCreatedEvent/>
     </triggerOnEvents>
     <dynamicTriggerConfiguration>false</dynamicTriggerConfiguration>
     <triggerConfigURL></triggerConfigURL>
     <triggerInformationAction/>
     </com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger>
     *
     *
     * @param triggerEvents Can be ommited and the plugin will user PatchsetCreated and DraftPublished by default. Provide in
     *                      show name format: ChangeMerged, CommentAdded, DraftPublished, PatchsetCreated, RefUpdated
     * @return
     */
    def gerrit(Closure contextClosure = null) {
        // See what they set up in the contextClosure before generating xml
        GerritContext gerritContext = new GerritContext()
        AbstractContextHelper.executeInContext(contextClosure, gerritContext)

        def nodeBuilder = new NodeBuilder()
        def gerritNode = nodeBuilder.'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger' {
            spec ''
            if (gerritContext.projects) {
                gerritProjects {
                    gerritContext.projects.each { GerritSpec project, List<GerritSpec> brancheSpecs ->
                        'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject' {
                            compareType project.type
                            pattern project.pattern
                            branches {
                                brancheSpecs.each { GerritSpec branch ->
                                    'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.Branch' {
                                        compareType branch.type
                                        pattern branch.pattern
                                    }
                                }
                            }
                        }
                    }
                }
            }
            silentMode false
            escapeQuotes true
            buildStartMessage ''
            buildFailureMessage ''
            buildSuccessfulMessage ''
            buildUnstableMessage ''
            buildNotBuiltMessage ''
            buildUnsuccessfulFilepath ''
            customUrl ''
            if (gerritContext.eventContext.eventShortNames) {
                triggerOnEvents {
                    gerritContext.eventContext.eventShortNames.each { eventShortName ->
                        "com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.Plugin${eventShortName}Event" ''
                    }
                }
            }
            gerritBuildStartedVerifiedValue 0
            gerritBuildStartedCodeReviewValue 0
            gerritBuildSuccessfulVerifiedValue 1
            gerritBuildSuccessfulCodeReviewValue 2
            gerritBuildFailedVerifiedValue '-2'
            gerritBuildFailedCodeReviewValue '-2'
            gerritBuildUnstableVerifiedValue '-1'
            gerritBuildUnstableCodeReviewValue '-1'
            gerritBuildNotBuiltVerifiedValue 0
            gerritBuildNotBuiltCodeReviewValue 0
            dynamicTriggerConfiguration false
            triggerConfigURL ''
            triggerInformationAction ''
        }

        // Apply their overrides
        if (gerritContext.configureClosure) {
            WithXmlAction action = new WithXmlAction(gerritContext.configureClosure)
            action.execute(gerritNode)
        }

        triggerNodes << gerritNode

    }

    /**
     * If set to <code>true</code>, Jenkins will parse the POMs of this project, and see if any of its snapshot
     * dependencies are built on this Jenkins as well. If so, Jenkins will set up build dependency relationship so that
     * whenever the dependency job is built and a new SNAPSHOT jar is created, Jenkins will schedule a build of this
     * project. Defaults to <code>false</code>.
     * @param checkSnapshotDependencies set to <code>true</code> to check snapshot dependencies
     */
    def snapshotDependencies(boolean checkSnapshotDependencies) {
        Preconditions.checkState jobType == JobType.Maven, "snapshotDependencies can only be applied for Maven jobs"
        withXmlActions << new WithXmlAction({
            it.children().removeAll { it instanceof Node && it.name() == "ignoreUpstremChanges" }
            it.appendNode "ignoreUpstremChanges", !checkSnapshotDependencies
        })
    }
}
