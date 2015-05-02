package javaposse.jobdsl.plugin;

import java.util.Map;

/**
 * The {@link DslEnvironment} can be used to transfer state between a {@link DslExtensionMethod}
 * and the listener methods in {@link ContextExtensionPoint}.
 *
 * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Extending-the-DSL">Extending the DSL</a>
 * @since 1.33
 */
public interface DslEnvironment extends Map<String, Object> {
}
