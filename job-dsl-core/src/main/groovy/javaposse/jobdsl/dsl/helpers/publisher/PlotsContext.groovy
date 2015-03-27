package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext

class PlotsContext implements Context {
    final List<PlotContext> plots = []

    void plot(String group, String dataStore, @DslContext(PlotContext) Closure plotClosure) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(group), 'group must not be null or empty')
        Preconditions.checkArgument(!Strings.isNullOrEmpty(dataStore), 'dataStore must not be null or empty')

        PlotContext plotContext = new PlotContext(group, dataStore)
        ContextHelper.executeInContext(plotClosure, plotContext)

        plots << plotContext
    }
}
