package javaposse.jobdsl.dsl.helpers.triggers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.Context

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

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
        this.type = Preconditions.checkNotNull(type, 'Inspection type must not be null!')
    }

    /**
     * Adds a JSON/XPATH path expression to the inspection.
     * @param path expression to add
     */
    def path(String path) {
        String p = Preconditions.checkNotNull(path, 'Path must not be null')
        Preconditions.checkArgument(!p.empty, 'Path given must not be empty')
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
        def expr = Preconditions.checkNotNull(exp, 'Regular expression must not be null')
        Preconditions.checkArgument(!expr.empty, 'Regular expressions must not be empty')
        try {
            Pattern.compile(expr)
        } catch (PatternSyntaxException pse) {
            throw new IllegalArgumentException("Syntax of pattern ${exp} is invalid: ${pse.message}")
        }

        expressions << exp
    }

}
