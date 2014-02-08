package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class PerformanceContext implements Context {

    int errorFailedThreshold = 0
    int errorUnstableThreshold = 0
    boolean modePerformancePerTestCase = false

    def jMeterParser = null
    def jMeterCsvParser = null
    def jUnitParser = null
    def jmeterSummarizerParser = null

    @Canonical
    static class JMeterParser implements Context {
        String glob = ''

        def glob(String glob) {
            this.glob = glob
        }
    }

    @Canonical
    static class JMeterCsvParser implements  Context {
        String glob = ''
        boolean skipFirstLine = false
        String delimiter = ','
        int timestampIdx = -1
        int elapsedIdx = -1
        int responseCodeIdx = -1
        int successIdx = -1
        int urlIdx = -1

        def glob(String glob) {
            this.glob = glob
        }

        def skipFirstLine(boolean skipFirstLine) {
            this.skipFirstLine = skipFirstLine
        }

        def delimiter(String delimiter) {
            this.delimiter = delimiter
        }

        def timestampIdx(int timestampIdx) {
            this.timestampIdx = timestampIdx
        }

        def elapsedIdx(int elapsedIdx) {
            this.elapsedIdx = elapsedIdx
        }

        def responseCodeIdx(int responseCodeIdx) {
            this.responseCodeIdx = responseCodeIdx
        }

        def successIdx(int successIdx) {
            this.successIdx = successIdx
        }

        def urlIdx(int urlIdx) {
            this.urlIdx = urlIdx
        }

    }

    @Canonical
    static class JUnitParser implements Context {
        String glob = ''

        def glob(String glob) {
            this.glob = glob
        }
    }

    @Canonical
    static class JmeterSummarizerParser implements Context {
        String glob = ''
        String logDateFormat = 'yyyy/mm/dd HH:mm:ss'

        def glob(String glob) {
            this.glob = glob
        }

        def logDateFormat(String logDateFormat) {
            this.logDateFormat = logDateFormat
        }
    }

    def jMeterParser(Closure closure) {
        jMeterParser = new JMeterParser()
        AbstractContextHelper.executeInContext(closure, jMeterParser)
    }

    def jMeterCsvParser(Closure closure) {
        jMeterCsvParser = new JMeterCsvParser()
        AbstractContextHelper.executeInContext(closure, jMeterCsvParser)
    }

    def jUnitParser(Closure closure) {
        jUnitParser = new JUnitParser()
        AbstractContextHelper.executeInContext(closure, jUnitParser)
    }

    def jmeterSummarizerParser(Closure closure) {
        jmeterSummarizerParser = new JmeterSummarizerParser()
        AbstractContextHelper.executeInContext(closure, jmeterSummarizerParser)
    }

}