package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

import java.lang.reflect.Constructor

class DslEnvironmentImpl implements DslEnvironment {
    private final Map<String, Object> data = [:]
    private final JobManagement jobManagement
    private final Item item

    DslEnvironmentImpl(JobManagement jobManagement, Item item) {
        this.jobManagement = jobManagement
        this.item = item
    }

    @Override
    <T extends Context> T createContext(Class<T> contextClass) {
        Constructor<?>[] constructors = contextClass.constructors
        if (constructors.length != 1) {
            throw new IllegalArgumentException('the context class must have exactly one public constructor')
        }
        Constructor<?> constructor = constructors[0]
        Class<?>[] parameterTypes = constructor.parameterTypes
        Object[] args = new Object[parameterTypes.length]
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].isInstance(jobManagement)) {
                args[i] = jobManagement
            } else if (parameterTypes[i].isInstance(item)) {
                args[i] = item
            } else if (parameterTypes[i].isInstance(this)) {
                args[i] = this
            } else {
                throw new IllegalArgumentException("unsupported constructor parameter type: ${parameterTypes[i].name}")
            }
        }
        contextClass.cast(constructor.newInstance(args))
    }

    @Override
    int size() {
        data.size()
    }

    @Override
    boolean isEmpty() {
        data.empty
    }

    @Override
    boolean containsKey(Object key) {
        data.containsKey(key)
    }

    @Override
    boolean containsValue(Object value) {
        data.containsValue(value)
    }

    @Override
    Object get(Object key) {
        data.get(key)
    }

    @Override
    Object put(String key, Object value) {
        data[key] = value
    }

    @Override
    Object remove(Object key) {
        data.remove(key)
    }

    @Override
    void putAll(Map<? extends String, ?> m) {
        data.putAll(m)
    }

    @Override
    void clear() {
        data.clear()
    }

    @Override
    Set<String> keySet() {
        data.keySet()
    }

    @Override
    Collection<Object> values() {
        data.values()
    }

    @Override
    Set<Map.Entry<String, Object>> entrySet() {
        data.entrySet()
    }
}
