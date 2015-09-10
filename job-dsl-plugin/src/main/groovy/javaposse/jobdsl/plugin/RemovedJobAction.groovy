package javaposse.jobdsl.plugin;

public enum RemovedJobAction {
    IGNORE("Ignore"),
    DISABLE("Disable"),
    DELETE("Delete");

    String displayName;

    RemovedJobAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}