package javaposse.jobdsl.dsl;

public class GeneratedConfigFile {
    private final String id;
    private final String name;

    public GeneratedConfigFile(String id, String name) {
        if (id == null || name == null) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneratedConfigFile)) return false;

        GeneratedConfigFile that = (GeneratedConfigFile) o;
        return id.equals(that.id);
    }

    @Override
    public String toString() {
        return "GeneratedConfigFile{" +
                "name='" + name + "', " +
                "id='" + id + "'" +
                "}";
    }
}
