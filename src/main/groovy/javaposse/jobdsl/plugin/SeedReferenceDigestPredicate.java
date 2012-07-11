package javaposse.jobdsl.plugin;

import com.google.common.base.Predicate;

public class SeedReferenceDigestPredicate implements Predicate<SeedReference> {
    private final String digest;

    public SeedReferenceDigestPredicate(String digest) {
        this.digest = digest;
    }

    @Override
    public boolean apply(SeedReference input) {
        return !digest.equals(input.digest);
    }
}
