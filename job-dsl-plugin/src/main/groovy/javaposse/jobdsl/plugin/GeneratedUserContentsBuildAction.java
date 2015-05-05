package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.Action;
import javaposse.jobdsl.dsl.GeneratedUserContent;

import java.util.Collection;
import java.util.Set;

public class GeneratedUserContentsBuildAction implements Action {
    public final Set<GeneratedUserContent> modifiedUserContents;

    public GeneratedUserContentsBuildAction(Collection<GeneratedUserContent> modifiedUserContents) {
        this.modifiedUserContents = Sets.newLinkedHashSet(modifiedUserContents);
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Generated User Content";
    }

    public String getUrlName() {
        return "generatedUserContent";
    }

    public Collection<GeneratedUserContent> getModifiedUserContents() {
        return modifiedUserContents;
    }
}
