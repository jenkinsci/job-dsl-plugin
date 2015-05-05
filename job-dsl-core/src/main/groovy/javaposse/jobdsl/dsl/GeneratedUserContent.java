package javaposse.jobdsl.dsl;

public class GeneratedUserContent {
    private final String path;

    public GeneratedUserContent(String path) {
        if (path == null) {
            throw new IllegalArgumentException();
        }
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeneratedUserContent that = (GeneratedUserContent) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return "GeneratedUserContent{" +
                "path='" + path + "'" +
                "}";
    }
}
