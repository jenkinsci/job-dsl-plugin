package javaposse.jobdsl.dsl

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * Global AST transformation that logs a deprecation warning when {@link Grab} or {@link Grapes} is used.
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class GrabDeprecationTransformation implements ASTTransformation {
    private final JobManagement jobManagement

    GrabDeprecationTransformation(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        source.AST.imports*.annotations.flatten().each { annotation ->
            if (annotation.classNode.name in ['groovy.lang.Grab', 'groovy.lang.Grapes']) {
                jobManagement.logDeprecationWarning('@Grab support', source.name, annotation.lineNumber)
            }
        }
    }
}
