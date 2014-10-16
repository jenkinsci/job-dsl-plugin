package javaposse.jobdsl.dsl.helpers.triggers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.ContextHelper
import javaposse.jobdsl.dsl.helpers.triggers.UrlTriggerInspectionContext.Inspection

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
        this.url = Preconditions.checkNotNull(url, 'The URL is required for urlTrigger()')
        Preconditions.checkArgument(url != '', 'URL must not be empty.')
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
        Inspection itype
        try {
            itype = Preconditions.checkNotNull(Inspection.valueOf(type), 'Inspection must not be null' as Object)
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Inspection must be one of ${Inspection.values()}")
        }

        UrlTriggerInspectionContext inspection = new UrlTriggerInspectionContext(itype)
        ContextHelper.executeInContext(inspectionClosure, inspection)

        inspections << inspection

    }
}
