package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import hudson.model.AbstractBuild;
import hudson.model.ItemGroup;
import hudson.model.Run;
import hudson.model.RunAction;
import hudson.model.View;
import hudson.model.ViewGroup;
import hudson.util.XStream2;
import javaposse.jobdsl.dsl.GeneratedView;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static javaposse.jobdsl.plugin.JenkinsJobManagement.getParent;
import static javaposse.jobdsl.plugin.JenkinsJobManagement.getItemNameFromFullName;

public class GeneratedViewsBuildAction implements RunAction {
    public final Set<GeneratedView> modifiedViews;

    private transient AbstractBuild owner;
    private LookupStrategy lookupStrategy = LookupStrategy.JENKINS_ROOT;

    public GeneratedViewsBuildAction(Collection<GeneratedView> modifiedJobs, LookupStrategy lookupStrategy) {
        this.modifiedViews = Sets.newLinkedHashSet(modifiedJobs);
        this.lookupStrategy = lookupStrategy;
    }

    /**
     * No task list item.
     */
    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Generated Views";
    }

    public String getUrlName() {
        return "generatedViews";
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onAttached(Run run) {
        if (run instanceof AbstractBuild) {
            owner = (AbstractBuild) run;
        }
    }

    @Override
    public void onBuildComplete() {
    }

    public LookupStrategy getLookupStrategy() {
        return lookupStrategy == null ? LookupStrategy.JENKINS_ROOT : lookupStrategy;
    }

    public Collection<GeneratedView> getModifiedViews() {
        return modifiedViews;
    }

    public Set<View> getViews() {
        Set<View> allGeneratedViews = Sets.newLinkedHashSet();
        if (modifiedViews != null) {
            for (GeneratedView generatedView : modifiedViews) {
                ItemGroup itemGroup = getParent(generatedView.getName(), getLookupStrategy().getContext(owner.getProject()));
                if (itemGroup instanceof ViewGroup) {
                    View view = ((ViewGroup) itemGroup).getView(getItemNameFromFullName(generatedView.getName()));
                    if (view != null) {
                        allGeneratedViews.add(view);
                    }
                }
            }
        }
        return allGeneratedViews;
    }

    // TODO Once we depend on Jenkins version 1.509.3 or higher we can implement the RunAction2 interface to set the AbstractBuild on load, instead of using this Converter.
    public static class ConverterImpl extends XStream2.PassthruConverter<GeneratedViewsBuildAction> {
        public ConverterImpl(XStream2 xStream) {
            super(xStream);
        }

        @Override
        protected void callback(GeneratedViewsBuildAction action, UnmarshallingContext context) {
            Iterator keys = context.keys();
            while (keys.hasNext()) {
                Object run = context.get(keys.next());
                if (run instanceof AbstractBuild) {
                    action.owner = (AbstractBuild) run;
                    return;
                }
            }
        }
    }
}
