package javaposse.jobdsl.plugin;

import javaposse.jobdsl.dsl.Context;

import java.util.Map;

/**
 * The {@link DslEnvironment} can be used to transfer state between a {@link DslExtensionMethod}
 * and the listener methods in {@link ContextExtensionPoint}.
 *
 * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Extending-the-DSL">Extending the DSL</a>
 * @since 1.33
 */
public interface DslEnvironment extends Map<String, Object> {
    /**
     * Creates an instance of the specified {@link Context} type.
     *
     * @param contextClass the type of the context to create
     * @param <T>          the type of the context to create
     * @return an instance of the specified type
     * @since 1.44
     */
    <T extends Context> T createContext(Class<T> contextClass);
}
