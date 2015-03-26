package javaposse.jobdsl.dsl;

public class GeneratedView {
    private final String name;

    public GeneratedView(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneratedView)) return false;

        GeneratedView that = (GeneratedView) o;
        return name.equals(that.name);
    }

    @Override
    public String toString() {
        return "GeneratedView{" +
                "name='" + name + "'" +
                "}";
    }
}
