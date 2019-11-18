package javaposse.jobdsl.plugin.casc;

import hudson.Extension;
import io.jenkins.plugins.casc.Attribute;
import io.jenkins.plugins.casc.ConfigurationContext;
import io.jenkins.plugins.casc.Configurator;
import io.jenkins.plugins.casc.ConfiguratorException;
import io.jenkins.plugins.casc.RootElementConfigurator;
import io.jenkins.plugins.casc.SecretSourceResolver;
import io.jenkins.plugins.casc.impl.attributes.MultivaluedAttribute;
import io.jenkins.plugins.casc.model.CNode;
import io.jenkins.plugins.casc.model.Mapping;
import javaposse.jobdsl.dsl.GeneratedItems;
import javaposse.jobdsl.plugin.JenkinsDslScriptLoader;
import javaposse.jobdsl.plugin.JenkinsJobManagement;
import javaposse.jobdsl.plugin.LookupStrategy;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static io.vavr.API.Try;
import static io.vavr.API.unchecked;

@Extension(optional = true, ordinal = -50) // Ordinal -50 Ensure it is loaded after GlobalJobDslSecurityConfiguration
@Restricted(NoExternalUse.class)
public class SeedJobConfigurator implements RootElementConfigurator<GeneratedItems[]> {

    @Nonnull
    @Override
    public String getName() {
        return "jobs";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getTarget() {
        return GeneratedItems[].class;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Set<Attribute<GeneratedItems[], ?>> describe() {
        return Collections.singleton(new MultivaluedAttribute("", ScriptSource.class));
    }

    @Override
    public GeneratedItems[] getTargetComponent(ConfigurationContext context) {
        return new GeneratedItems[0]; // Doesn't really make sense
    }

    @Nonnull
    @Override
    public GeneratedItems[] configure(CNode config, ConfigurationContext context) throws ConfiguratorException {
        Map<String, String> env = new HashMap<>(System.getenv());
        JenkinsJobManagement management = new JenkinsJobManagement(System.out, env, null, null, LookupStrategy.JENKINS_ROOT);
        Configurator<ScriptSource> configurator = context.lookupOrFail(ScriptSource.class);
        return config.asSequence().stream()
            .flatMap(source -> processProvidedEnv(source, context, env))
            .map(source -> getActualValue(source, context))
            .map(source -> getScriptFromSource(source, context, configurator))
            .map(script -> generateFromScript(script, management))
            .toArray(GeneratedItems[]::new);
    }

    @Override
    public GeneratedItems[] check(CNode config, ConfigurationContext context) {
        return new GeneratedItems[0];
    }

    @Nonnull
    @Override
    public List<Configurator<GeneratedItems[]>> getConfigurators(ConfigurationContext context) {
        return Collections.singletonList(context.lookup(ScriptSource.class));
    }

    @CheckForNull
    @Override
    public CNode describe(GeneratedItems[] instance, ConfigurationContext context) {
        return null;
    }

    private Stream<CNode> processProvidedEnv(CNode source, ConfigurationContext context, Map<String, String> env) {
        Map.Entry<String, CNode> entry = unchecked(() -> source.asMapping().entrySet().iterator().next()).apply();
        if (entry.getKey().equals("providedEnv")) {
            unchecked(entry.getValue()::asMapping).apply().entrySet().forEach(envEntry ->
                env.put(envEntry.getKey(), revealSourceOrGetValue(envEntry, context)));
            return Stream.empty();
        }
        return Stream.of(source);
    }

    private CNode getActualValue(CNode config, ConfigurationContext context) {
        return unchecked(() -> config.asMapping().entrySet().stream().findFirst()).apply()
            .map(entry -> {
                Mapping mapping = new Mapping();
                mapping.put(entry.getKey(), revealSourceOrGetValue(entry, context));
                return (CNode) mapping;
            }).orElse(config);
    }

    private String revealSourceOrGetValue(Map.Entry<String, CNode> entry, ConfigurationContext context) {
        String value = unchecked(() -> entry.getValue().asScalar().getValue()).apply();
        return SecretSourceResolver.resolve(context, value);
    }

    private GeneratedItems generateFromScript(String script, JenkinsJobManagement management) {
        return unchecked(() ->
            Try(() -> new JenkinsDslScriptLoader(management).runScript(script))
                .getOrElseThrow(t -> new ConfiguratorException(this, "Failed to execute script with hash " + script.hashCode(), t))).apply();
    }

    private String getScriptFromSource(CNode source, ConfigurationContext context, Configurator<ScriptSource> configurator) {
        return unchecked(() ->
            Try(() -> configurator.configure(source, context))
                .getOrElseThrow(t -> new ConfiguratorException(this, "Failed to retrieve job-dsl script", t))
                .getScript()).apply();
    }
}
