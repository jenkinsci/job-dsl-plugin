package javaposse.jobdsl.plugin;

import javaposse.jobdsl.dsl.GeneratedUserContent;

import java.util.Collection;

public class GeneratedUserContentsBuildAction extends GeneratedObjectsBuildAction<GeneratedUserContent> {
    public GeneratedUserContentsBuildAction(Collection<GeneratedUserContent> modifiedUserContents) {
        super(modifiedUserContents);
    }

    public String getDisplayName() {
        return "Generated User Content";
    }

    public String getUrlName() {
        return "generatedUserContent";
    }
}
