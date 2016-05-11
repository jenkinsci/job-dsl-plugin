package javaposse.jobdsl.plugin.actions;

import hudson.model.ItemGroup;
import hudson.model.View;
import hudson.model.ViewGroup;
import javaposse.jobdsl.dsl.GeneratedView;
import javaposse.jobdsl.plugin.LookupStrategy;
import org.apache.commons.io.FilenameUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class GeneratedViewsBuildAction extends GeneratedObjectsBuildRunAction<GeneratedView> {
    @SuppressWarnings("unused")
    private transient Set<GeneratedView> modifiedViews;

    private LookupStrategy lookupStrategy = LookupStrategy.JENKINS_ROOT;

    public GeneratedViewsBuildAction(Collection<GeneratedView> modifiedViews, LookupStrategy lookupStrategy) {
        super(modifiedViews);
        this.lookupStrategy = lookupStrategy;
    }

    private LookupStrategy getLookupStrategy() {
        return lookupStrategy == null ? LookupStrategy.JENKINS_ROOT : lookupStrategy;
    }

    public Set<View> getViews() {
        Set<View> allGeneratedViews = new LinkedHashSet<View>();
        for (GeneratedView generatedView : getModifiedObjects()) {
            ItemGroup itemGroup = getLookupStrategy().getParent(owner.getProject(), generatedView.getName());
            if (itemGroup instanceof ViewGroup) {
                View view = ((ViewGroup) itemGroup).getView(FilenameUtils.getName(generatedView.getName()));
                if (view != null) {
                    allGeneratedViews.add(view);
                }
            }
        }
        return allGeneratedViews;
    }

    @SuppressWarnings("unused")
    private Object readResolve() {
        return modifiedViews == null ? this : new GeneratedViewsBuildAction(modifiedViews, lookupStrategy);
    }
}
