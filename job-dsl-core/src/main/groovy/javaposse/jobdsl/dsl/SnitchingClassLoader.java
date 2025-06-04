package javaposse.jobdsl.dsl;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;

@Restricted(DoNotUse.class)
public class SnitchingClassLoader extends ClassLoader {
    public SnitchingClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Package[] getPackages() {
        return super.getPackages();
    }
}
