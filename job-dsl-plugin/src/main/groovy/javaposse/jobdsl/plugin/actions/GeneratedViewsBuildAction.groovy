package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import hudson.model.ItemGroup
import hudson.model.View
import hudson.model.ViewGroup
import javaposse.jobdsl.dsl.GeneratedView
import javaposse.jobdsl.plugin.LookupStrategy
import org.apache.commons.io.FilenameUtils

import static javaposse.jobdsl.plugin.LookupStrategy.JENKINS_ROOT

class GeneratedViewsBuildAction extends GeneratedObjectsRunAction<GeneratedView> {
    @SuppressWarnings('UnnecessaryTransientModifier')
    @Deprecated
    private transient Set<GeneratedView> modifiedViews

    protected final LookupStrategy lookupStrategy

    GeneratedViewsBuildAction(Collection<GeneratedView> modifiedViews, LookupStrategy lookupStrategy) {
        super(modifiedViews)
        this.lookupStrategy = lookupStrategy
    }

    Iterable<View> getViews() {
        Set<View> allGeneratedViews = []
        for (GeneratedView generatedView : modifiedObjects) {
            ItemGroup itemGroup = lookupStrategy.getParent(owner.parent, generatedView.name)
            if (itemGroup instanceof ViewGroup) {
                ViewGroup viewGroup = itemGroup as ViewGroup
                View view = viewGroup.getView(FilenameUtils.getName(generatedView.name))
                if (view != null) {
                    allGeneratedViews << view
                }
            }
        }
        allGeneratedViews.toSorted(Comparators.VIEW_COMPARATOR)
    }

    @Override
    Collection<? extends Action> getProjectActions() {
        Collections.singleton(new GeneratedViewsAction(owner.parent))
    }

    @SuppressWarnings(['UnusedPrivateMethod', 'GroovyUnusedDeclaration'])
    private Object readResolve() {
        if (lookupStrategy == null || modifiedObjects == null) {
            new GeneratedViewsBuildAction(modifiedObjects ?: modifiedViews, lookupStrategy ?: JENKINS_ROOT)
        } else {
            this
        }
    }
}
