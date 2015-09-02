package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ArchivePerformanceJMeterContext implements Context {

    String glob

    void glob(String glob) {
        this.glob = glob
    }
}
