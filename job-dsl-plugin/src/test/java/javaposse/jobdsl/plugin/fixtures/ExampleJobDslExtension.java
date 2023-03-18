package javaposse.jobdsl.plugin.fixtures;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.JobProperty;
import javaposse.jobdsl.dsl.helpers.properties.PropertiesContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslEnvironment;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Extension(optional = true)
public class ExampleJobDslExtension extends ContextExtensionPoint {
    private static final String PREFIX = "example.";

    @DslExtensionMethod(context = PropertiesContext.class)
    public Object example(String optionA, String configJson, DslEnvironment dslEnvironment) {
        dslEnvironment.put(PREFIX + optionA, configJson);
        return new SomeValueObject(optionA);
    }

    @Override
    public void notifyItemCreated(Item item,
                                  DslEnvironment dslEnvironment) {
        notifyItemUpdated(item, dslEnvironment);
    }

    @Override
    public void notifyItemUpdated(Item item,
                                  DslEnvironment dslEnvironment) {
        for (Map.Entry<String, Object> entry : dslEnvironment.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(PREFIX)) {
                String fileName = key.substring(PREFIX.length()) + ".json";
                File configFile = new File(item.getRootDir(), fileName);
                try {
                    FileUtils.write(configFile, entry.getValue().toString());
                } catch (IOException e) {
                    // handle exception
                }
            }
        }
    }

    public static class SomeValueObject extends JobProperty<Job<?, ?>> {
        private String value;

        public SomeValueObject(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
