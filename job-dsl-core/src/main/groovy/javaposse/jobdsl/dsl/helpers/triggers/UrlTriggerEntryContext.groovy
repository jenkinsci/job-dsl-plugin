package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.helpers.triggers.UrlTriggerInspectionContext.Inspection

/**
 * Configuration container for a monitored URL.
 */
class UrlTriggerEntryContext implements Context {
    /**
     * Enumeration of defined checks.
     */
    enum Check {
        /**
         * Check the response status.
         */
        status,

        /**
         * Check the ETag information of the URL.
         */
        etag,

        /**
         * Check the last modified date.
         */
        lastModified
    }

    String url
    int statusCode = 200
    long timeout = 300
    boolean proxyActivated = false
    EnumSet<Check> checks = EnumSet.noneOf(Check)
    List<UrlTriggerInspectionContext> inspections = []

    /**
     * Creates a new entry for a monitored URL.
     *
     * @param url Required URL to monitor
     */
    UrlTriggerEntryContext(String url) {
        Preconditions.checkNotNullOrEmpty(url, 'The URL is required for urlTrigger')
        this.url = url
    }

    /**
     * Enables/Disables the use of the global proxy that is configured for Jenkins.
     *
     * Defaults to <code>false</code>
     *
     * @param active <code>true</code> to use a proxy
     */
    void proxy(boolean active) {
        this.proxyActivated = active
    }

    /**
     * Define the expected status code of the response.
     *
     * Defaults to 200.
     * Needs to be used with {@code check('status')} to be useful.
     *
     * @param statusCode status code to expect from URL
     */
    void status(int statusCode) {
        this.statusCode = statusCode
    }

    /**
     * Defines how many seconds the trigger will wait when checking the URL.
     *
     * Defaults to 300 seconds.
     *
     * @param timeout number of seconds to wait for response
     */
    void timeout(long timeout) {
        this.timeout = timeout
    }

    /**
     * Enables checks to perform for URL.
     *
     * Can be one of {@code 'status'} (check status code), {@code 'etag'} (check the ETag) and {@code 'lastModified'}
     * (check the last modified date).
     *
     * @param performCheck check to perform
     */
    void check(String performCheck) {
        Preconditions.checkNotNull(performCheck, 'Check must not be null')

        try {
            checks << Check.valueOf(performCheck)
        } catch (IllegalArgumentException ignore) {
            throw new DslScriptException("Check must be one of: ${Check.values()}")
        }
    }

    /**
     * Adds inspections of the returned content.
     *
     * Can be one of {@code 'change'}, {@code 'json'}, {@code 'xml'} or {@code 'text}.
     *
     * @param type type of inspection to use
     * @param inspectionClosure for configuring RegExps/Path expressions for xml, text and json
     */
    void inspection(String type, @DslContext(UrlTriggerInspectionContext) Closure inspectionClosure = null) {
        Preconditions.checkNotNull(type, 'Inspection must not be null')

        Inspection itype
        try {
            itype = Inspection.valueOf(type)
        } catch (IllegalArgumentException ignore) {
            throw new DslScriptException("Inspection must be one of ${Inspection.values()}")
        }

        UrlTriggerInspectionContext inspection = new UrlTriggerInspectionContext(itype)
        ContextHelper.executeInContext(inspectionClosure, inspection)

        inspections << inspection
    }
}
