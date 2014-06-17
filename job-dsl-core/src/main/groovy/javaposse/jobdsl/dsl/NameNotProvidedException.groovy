package javaposse.jobdsl.dsl

class NameNotProvidedException extends RuntimeException {
    NameNotProvidedException() {
        super('No name was provided for the Job or View.')
    }
}
