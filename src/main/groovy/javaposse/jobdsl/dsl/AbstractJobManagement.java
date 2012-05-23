package javaposse.jobdsl.dsl;

import java.io.PrintStream;

/**
 * Abstract version of JobManagement to minimize impact on future API changes
 */
public abstract class AbstractJobManagement implements JobManagement {
    protected PrintStream out;

    protected AbstractJobManagement(PrintStream out) {
        this.out = out;
    }

    protected AbstractJobManagement() {
        this(System.out);
    }

    @Override
    public PrintStream getOutputStream() {
        return out;
    }

}
