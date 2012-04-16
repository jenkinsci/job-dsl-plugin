package javaposse.jobdsl.dsl

/**
 * @author aharmel-law
 */
class JobNameNotProvidedException extends Throwable {
    public JobNameNotProvidedException() {
        super("The provided job name was not provided");
    }
}
