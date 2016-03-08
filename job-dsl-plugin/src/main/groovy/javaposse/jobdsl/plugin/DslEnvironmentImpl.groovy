package javaposse.jobdsl.plugin;

import javaposse.jobdsl.dsl.Context;
import javaposse.jobdsl.dsl.Item;
import javaposse.jobdsl.dsl.JobManagement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DslEnvironmentImpl implements DslEnvironment {
    private final Map<String, Object> data = new HashMap<String, Object>();
    private final JobManagement jobManagement;
    private final Item item;

    public DslEnvironmentImpl(JobManagement jobManagement, Item item) {
        this.jobManagement = jobManagement;
        this.item = item;
    }

    @Override
    public <T extends Context> T createContext(Class<T> contextClass) throws IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor<?>[] constructors = contextClass.getConstructors();
        if (constructors.length != 1) {
            throw new IllegalArgumentException("the context class must have exactly one public constructor");
        }
        Constructor<?> constructor = constructors[0];
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].isInstance(jobManagement)) {
                args[i] = jobManagement;
            } else if (parameterTypes[i].isInstance(item)) {
                args[i] = item;
            } else {
                throw new IllegalArgumentException("unsupported constructor parameter type: " + parameterTypes[i].getName());
            }
        }
        return contextClass.cast(constructor.newInstance(args));
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return data.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return data.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return data.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        data.putAll(m);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public Set<String> keySet() {
        return data.keySet();
    }

    @Override
    public Collection<Object> values() {
        return data.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return data.entrySet();
    }
}
