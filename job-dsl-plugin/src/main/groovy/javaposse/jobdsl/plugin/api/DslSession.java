package javaposse.jobdsl.plugin.api;

import java.util.HashMap;
import java.util.Map;

public class DslSession {
    private static final ThreadLocal<DslSession> CURRENT_SESSION = new ThreadLocal<DslSession>();

    private final Map<String, Object> data = new HashMap<String, Object>();

    public Object getData(String key) {
        return data.get(key);
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

    public static void setCurrentSession(DslSession dslSession) {
        CURRENT_SESSION.set(dslSession);
    }

    public static DslSession getCurrentSession() {
        DslSession dslSession = CURRENT_SESSION.get();
        if (dslSession == null) {
            throw new IllegalStateException("current DSL session has not been set");
        }
        return dslSession;
    }

    public static void clearCurrentSession() {
        CURRENT_SESSION.remove();
    }
}
