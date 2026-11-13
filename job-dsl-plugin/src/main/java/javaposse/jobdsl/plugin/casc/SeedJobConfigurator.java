package javaposse.jobdsl.plugin.casc;

import static io.vavr.API.Try;
import static io.vavr.API.unchecked;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.jenkins.plugins.casc.Attribute;
import io.jenkins.plugins.casc.ConfigurationContext;
import io.jenkins.plugins.casc.Configurator;
import io.jenkins.plugins.casc.ConfiguratorException;
import io.jenkins.plugins.casc.RootElementConfigurator;
import io.jenkins.plugins.casc.impl.attributes.MultivaluedAttribute;
import io.jenkins.plugins.casc.model.CNode;
import io.jenkins.plugins.casc.model.Mapping;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javaposse.jobdsl.dsl.GeneratedItems;
import javaposse.jobdsl.dsl.ScriptRequest;
import javaposse.jobdsl.plugin.JenkinsDslScriptLoader;
import javaposse.jobdsl.plugin.JenkinsJobManagement;
import javaposse.jobdsl.plugin.LookupStrategy;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

@Extension(optional = true, ordinal = -50) // Ordinal -50 Ensure it is loaded after GlobalJobDslSecurityConfiguration
@Restricted(NoExternalUse.class)
public class SeedJobConfigurator implements RootElementConfigurator<GeneratedItems[]> {

    @NonNull
    @Override
    public String getName() {
        return "jobs";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getTarget() {
        return GeneratedItems[].class;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public Set<Attribute<GeneratedItems[], ?>> describe() {
        return Collections.singleton(new MultivaluedAttribute("", ScriptSource.class));
    }

    @Override
    public GeneratedItems[] getTargetComponent(ConfigurationContext context) {
        return new GeneratedItems[0]; // Doesn't really make sense
    }

    @NonNull
    @Override
    public GeneratedItems[] configure(CNode config, ConfigurationContext context) throws ConfiguratorException {
        Map<String, String> env = new HashMap<>(System.getenv());
        JenkinsJobManagement management =
                new JenkinsJobManagement(System.out, env, null, null, LookupStrategy.JENKINS_ROOT);
        Configurator<ScriptSource> configurator = context.lookupOrFail(ScriptSource.class);
        return config.asSequence().stream()
                .flatMap(source -> processProvidedEnv(source, context, env))
                .map(source -> extractJobDefinition(source, context, configurator))
                .map(jobDef -> generateFromScript(jobDef.scriptText(), management, jobDef.classpathUrls()))
                .toArray(GeneratedItems[]::new);
    }

    @Override
    public GeneratedItems[] check(CNode config, ConfigurationContext context) {
        return new GeneratedItems[0];
    }

    @NonNull
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
        Map.Entry<String, CNode> entry =
                unchecked(() -> source.asMapping().entrySet().iterator().next()).apply();
        if (entry.getKey().equals("providedEnv")) {
            unchecked(entry.getValue()::asMapping)
                    .apply()
                    .forEach((key, value) -> env.put(key, revealSourceOrGetValue(value, context)));
            return Stream.empty();
        }
        return Stream.of(source);
    }

    private record JobDefinition(String scriptText, List<URL> classpathUrls) {}

    private JobDefinition extractJobDefinition(
            CNode source, ConfigurationContext context, Configurator<ScriptSource> configurator) {
        return unchecked(() -> Try(() -> {
                            Mapping mapping = source.asMapping();
                            List<URL> classpathUrls = new ArrayList<>();
                            Mapping targetMapping = new Mapping();

                            for (Map.Entry<String, CNode> entry : mapping.entrySet()) {
                                String key = entry.getKey();
                                if ("classpath".equals(key)) {
                                    CNode classpathNode = entry.getValue();
                                    if (classpathNode.getType() == CNode.Type.SEQUENCE) {
                                        for (CNode item : classpathNode.asSequence()) {
                                            String path = revealSourceOrGetValue(item, context);
                                            if (path == null || path.trim().isEmpty()) {
                                                throw new ConfiguratorException(
                                                        this, "'classpath' contains an empty path");
                                            }
                                            classpathUrls.add(new File(path.trim())
                                                    .toURI()
                                                    .toURL());
                                        }
                                    } else {
                                        throw new ConfiguratorException(
                                                this, "'classpath' must be a sequence of paths");
                                    }
                                } else if ("file".equals(key) || "script".equals(key) || "url".equals(key)) {
                                    targetMapping.put(key, revealSourceOrGetValue(entry.getValue(), context));
                                }
                            }

                            if (targetMapping.isEmpty()) {
                                throw new ConfiguratorException(this, "Job definition must contain a script source");
                            }

                            String scriptText = getScriptFromSource(targetMapping, context, configurator);
                            return new JobDefinition(scriptText, classpathUrls);
                        })
                        .getOrElseThrow(t -> {
                            if (t instanceof ConfiguratorException) return (ConfiguratorException) t;
                            return new ConfiguratorException(this, "Failed to parse job definition", t);
                        }))
                .apply();
    }

    private String revealSourceOrGetValue(CNode node, ConfigurationContext context) {
        String value = unchecked(() -> node.asScalar().getValue()).apply();
        return context.getSecretSourceResolver().resolve(value);
    }

    private GeneratedItems generateFromScript(String script, JenkinsJobManagement management, List<URL> classpathUrls) {
        return unchecked(() -> Try(() -> {
                            List<URL> urls = new ArrayList<>(classpathUrls);
                            urls.add(new File(".").toURI().toURL());

                            ScriptRequest request =
                                    new ScriptRequest(script, urls.toArray(new URL[0]), false, null, null);
                            return new JenkinsDslScriptLoader(management)
                                    .runScripts(Collections.singletonList(request));
                        })
                        .getOrElseThrow(t -> new ConfiguratorException(
                                this, "Failed to execute script with hash " + script.hashCode(), t)))
                .apply();
    }

    private String getScriptFromSource(
            CNode source, ConfigurationContext context, Configurator<ScriptSource> configurator) {
        return unchecked(() -> Try(() -> configurator.configure(source, context))
                        .getOrElseThrow(t -> new ConfiguratorException(this, "Failed to retrieve job-dsl script", t))
                        .getScript())
                .apply();
    }
}
