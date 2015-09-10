package javaposse.jobdsl.plugin;

public enum RemovedViewAction {
    IGNORE("Ignore"),
    DELETE("Delete");

    String displayName;

    RemovedViewAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
