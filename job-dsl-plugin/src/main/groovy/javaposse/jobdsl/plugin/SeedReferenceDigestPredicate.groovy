package javaposse.jobdsl.plugin

import com.google.common.base.Predicate

class SeedReferenceDigestPredicate implements Predicate<SeedReference> {
    private final String digest

    SeedReferenceDigestPredicate(String digest) {
        this.digest = digest
    }

    @Override
    boolean apply(SeedReference input) {
        digest != input.digest
    }
}
