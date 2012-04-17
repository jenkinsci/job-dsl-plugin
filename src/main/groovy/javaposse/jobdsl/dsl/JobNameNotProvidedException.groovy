package javaposse.jobdsl.dsl;

/**
 * @author aharmel-law
 */
public class JobNameNotProvidedException extends Throwable {
    public JobNameNotProvidedException() {
        super("No name was provided for the Job.");
    }
}
