package javaposse.jobdsl.plugin;

public enum RelativeNameContext {
    JENKINS_ROOT("Jenkins Root"),
    SEED_JOB("Seed Job");

    String displayName;

    RelativeNameContext(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}