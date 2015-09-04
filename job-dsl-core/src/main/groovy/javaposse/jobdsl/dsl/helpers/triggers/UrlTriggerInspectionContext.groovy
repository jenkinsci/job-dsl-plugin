package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Preconditions

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/** Context for configuring inspections that support paths/RegExps. */
class UrlTriggerInspectionContext implements Context {
    /** Enumeration of inspections with their respective element names in XML */
    enum Inspection {
        /** Simple monitor for change of MD5 hash. no nested elements.*/
        change('org.jenkinsci.plugins.urltrigger.content.SimpleContentType', null, null, null),

        /** JSON content */
        json('org.jenkinsci.plugins.urltrigger.content.JSONContentType', 'jsonPaths',
                'org.jenkinsci.plugins.urltrigger.content.JSONContentEntry', 'jsonPath'),

        /** TEXT content */
        text('org.jenkinsci.plugins.urltrigger.content.TEXTContentType', 'regExElements',
                'org.jenkinsci.plugins.urltrigger.content.TEXTContentEntry', 'regEx'),

        /** XML content */
        xml('org.jenkinsci.plugins.urltrigger.content.XMLContentType', 'xPaths',
                'org.jenkinsci.plugins.urltrigger.content.XMLContentEntry', 'xPath')

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
    List<String> expressions = []

    UrlTriggerInspectionContext(Inspection type) {
        Preconditions.checkNotNull(type, 'Inspection type must not be null!')
        this.type = type
    }

    /**
     * Adds a JSON/XPATH path expression to the inspection.
     *
     * @param path expression to add
     */
    void path(String path) {
        Preconditions.checkNotNullOrEmpty(path, 'Path must not be null or empty')
        expressions << path
    }

    /**
     * Adds a RegExp for TEXT inspections.
     *
     * Checks that the given Regexp is actually compilable to a Java RegExp.
     *
     * @param exp regular expression to add
     */
    void regexp(String exp) {
        Preconditions.checkNotNullOrEmpty(exp, 'Regular expression must not be null or empty')
        try {
            Pattern.compile(exp)
        } catch (PatternSyntaxException pse) {
            throw new DslScriptException("Syntax of pattern ${exp} is invalid: ${pse.message}")
        }

        expressions << exp
    }
}
