package javaposse.jobdsl.dsl

/**
 * @author aharmel-law
 */
public class NameNotProvidedException extends RuntimeException {
    public NameNotProvidedException() {
        super("No name was provided for the Job or View.")
    }
}
