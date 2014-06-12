package javaposse.jobdsl.dsl

/**
 * @author aharmel-law
 */
class NameNotProvidedException extends RuntimeException {
    NameNotProvidedException() {
        super("No name was provided for the Job or View.")
    }
}
