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

    int startedCodeReview =0
    int startedVerified =0

    int successfulCodeReview =0
    int successfulVerified =1

    int failedCodeReview =0
    int failedVerified = -1

    int unstableCodeReview =0
    int unstableVerified =0

    int notBuiltCodeReview =0
    int notBuiltVerified =0

    def buildStarted(int verified, int codeReview){
        startedVerified = verified
        startedCodeReview = codeReview
    }

    def buildStarted(Object verified, Object codeReview){
        buildStarted(
            Integer.parseInt(verified.toString()),
            Integer.parseInt(codeReview.toString())
        )
    }


    def buildSuccessful(int verified, int codeReview){
        successfulVerified = verified
        successfulCodeReview = codeReview
    }

    def buildSuccessful(Object verified, Object codeReview){
        buildSuccessful(
            Integer.parseInt(verified.toString()),
            Integer.parseInt(codeReview.toString())
        )
    }

    def buildFailed(int verified, int codeReview){
        failedVerified = verified
        failedCodeReview = codeReview
    }

    def buildFailed(Object verified, Object codeReview){
        buildFailed(
            Integer.parseInt(verified.toString()),
            Integer.parseInt(codeReview.toString())
        )
    }

    def buildUnstable(int verified, int codeReview){
        unstableVerified = verified
        unstableCodeReview = codeReview
    }

    def buildUnstable(Object verified, Object codeReview){
        buildUnstable(
            Integer.parseInt(verified.toString()),
            Integer.parseInt(codeReview.toString())
        )
    }

    def buildNotBuilt(int verified, int codeReview){
        notBuiltVerified = verified
        notBuiltCodeReview = codeReview
    }

    def buildNotBuilt(Object verified, Object codeReview){
        buildNotBuilt(
            Integer.parseInt(verified.toString()),
            Integer.parseInt(codeReview.toString())
        )
    }

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

/** Context for configuring inspections that support paths/RegExps. */
class UrlTriggerInspectionContext implements Context {

    /** Enumeration of inspections with their respective element names in XML */
    enum Inspection {
        /** Simple monitor for change of MD5 hash. no nested elements.*/
        change('org.jenkinsci.plugins.urltrigger.content.SimpleContentType', null, null, null),

        /** JSON content */
        json('org.jenkinsci.plugins.urltrigger.content.JSONContentType', 'jsonPaths', 'org.jenkinsci.plugins.urltrigger.content.JSONContentEntry', 'jsonPath'),

        /** TEXT content */
        text('org.jenkinsci.plugins.urltrigger.content.TEXTContentType', 'regExElements', 'org.jenkinsci.plugins.urltrigger.content.TEXTContentEntry', 'regEx'),

        /** XML content */
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

    /**
     * Adds a JSON/XPATH path expression to the inspection.
     * @param path expression to add
     */
    def path(String path) {
        String p = Preconditions.checkNotNull(path, "Path must not be null")
        Preconditions.checkArgument(!p.empty, "Path given must not be empty")
        expressions << p
    }

    /**
     * Adds a RegExp for TEXT inspections.
     *
     * Checks that the given Regexp is actually compilable to a Java RegExp.
     *
     * @param exp regular expression to add
     */
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

/** Configuration container for a monitored URL.*/
class UrlTriggerEntryContext implements Context {

    /** Enumeration of defined checks */
    enum Check {
        /** Check the response status */
        status,

        /** Check the ETag information of the URL*/
        etag,

        /** Check the last modified date */
        lastModified
    }

    /* Currently not usable due to encryption dependencies on Jenkins instance
    def username
    def password
    */

    def url
    def statusCode = 200
    def timeout = 300
    def proxyActivated = false
    EnumSet<Check> checks = EnumSet.noneOf(Check)
    def inspections = []

    /**
     * Creates a new entry for a monitored URL.
     *
     * @param url Required URL to monitor
     */
    UrlTriggerEntryContext(String url) {
        this.url = Preconditions.checkNotNull(url, "The URL is required for urlTrigger()")
        Preconditions.checkArgument(url != "", "URL must not be empty.")
        this.statusCode = statusCode
        this.timeout = timeout
    }

    /**
     * Enables/Disables the use of the global proxy that is configured for Jenkins.
     *
     * Defaults to <code>false</code>
     * @param active <code>true</code> to use a proxy
     */
    def proxy(boolean active) {
        this.proxyActivated = active
    }

    /**
     * Define the expected status code of the response.
     *
     * Defaults to 200.
     * Needs to be used with check('status') to be useful.
     *
     * @param statusCode status code to expect from URL
     */
    def status(int statusCode) {
        this.statusCode = statusCode
    }

    /**
     * Defines how many seconds the trigger will wait when checking the URL.
     *
     * Defaults to 300 seconds.
     *
     * @param timeout number of seconds to wait for response
     */
    def timeout(long timeout) {
        this.timeout = timeout
    }

    /**
     * Enables checks to perform for URL.
     *
     * Can be one of:
     *
     * 'status' (Check status code)
     * 'etag' (Check the ETag)
     * 'lastModified' (Check the last modified date)
     *
     * @param performCheck check to perform
     */
    def check(String performCheck) {
        Check check

        try {
            check = Preconditions.checkNotNull(Check.valueOf(performCheck), 'Check must not be null' as Object)
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Check must be one of: ${Check.values()}")
        }

        checks << check
    }

    /**
     * Adds inspections of the returned content.
     *
     * Can be one of:
     * 'change'
     * 'json'
     * 'xml'
     * 'text'
     *
     * @param type type of inspection to use
     * @param inspectionClosure for configuring RegExps/Path expressions for xml, text and json
     * @return
     */
    def inspection(String type, Closure inspectionClosure = null) {

        UrlTriggerInspectionContext.Inspection itype
        try {
        itype = Preconditions.checkNotNull(
                UrlTriggerInspectionContext.Inspection.valueOf(type),
                'Inspection must not be null' as Object)
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Inspection must be one of ${UrlTriggerInspectionContext.Inspection.values()}")
        }

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

/**
 * Top level context for configuring the URL trigger functionality.
 */
class UrlTriggerContext implements Context {
    Closure configureClosure
    def label
    def entries = []
    String crontab = 'H/5 * * * *'

    UrlTriggerContext(String cron = null) {
        if (cron) this.crontab = cron
    }

    /** Adds configure closure for overriding the generated XML */
    def configure(Closure configureClosure) {
        this.configureClosure = configureClosure
    }

    /** restrict execution to label */
    def restrictToLabel(String label) {
        this.label = label
    }

    /** Sets the cron schedule */
    def cron(String cron) {
        this.crontab = cron
    }

    /** adds a monitored URL to the trigger. */
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
     * Adds DSL  for adding and configuring the URL trigger plugin to a job.
     *
     * Uses a default cron execution schedule "H/5 * * * *", every 5 minutes with some jitter to prevent load pikes.
     *
     * @param contextClosure closure for configuring the context
     */
    def urlTrigger(Closure contextClosure) {
        urlTrigger(null, contextClosure)
    }

    /**
     * Adds DSL  for adding and configuring the URL trigger plugin to a job.
     *
     * @param crontab crontab execution spec
     * @param contextClosure closure for configuring the context
     */
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
     * Trigger that runs jobs on push notifications from Github/Github enterprise
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
            gerritBuildStartedVerifiedValue Integer.toString(gerritContext.startedVerified)
            gerritBuildStartedCodeReviewValue Integer.toString(gerritContext.startedCodeReview)
            gerritBuildSuccessfulVerifiedValue Integer.toString(gerritContext.successfulVerified)
            gerritBuildSuccessfulCodeReviewValue Integer.toString(gerritContext.successfulCodeReview)
            gerritBuildFailedVerifiedValue Integer.toString(gerritContext.failedVerified)
            gerritBuildFailedCodeReviewValue Integer.toString(gerritContext.failedCodeReview)
            gerritBuildUnstableVerifiedValue Integer.toString(gerritContext.unstableVerified)
            gerritBuildUnstableCodeReviewValue Integer.toString(gerritContext.unstableCodeReview)
            gerritBuildNotBuiltVerifiedValue Integer.toString(gerritContext.notBuiltVerified)
            gerritBuildNotBuiltCodeReviewValue Integer.toString(gerritContext.notBuiltCodeReview)
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
