package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

class UpstreamContext implements Context {
    public static final THRESHOLD_COLOR_MAP = ['SUCCESS': 'BLUE', 'UNSTABLE': 'YELLOW', 'FAILURE': 'RED']
    public static final THRESHOLD_ORDINAL_MAP = ['SUCCESS': 0, 'UNSTABLE': 1, 'FAILURE': 2]
}
