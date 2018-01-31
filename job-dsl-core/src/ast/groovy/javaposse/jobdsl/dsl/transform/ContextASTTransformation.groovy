package javaposse.jobdsl.dsl.transform

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static groovy.lang.Closure.DELEGATE_FIRST

/**
 * Global AST transformation for enabling IDE support for nested DSL contexts.
 *
 * A <code>@groovy.lang.DelegatesTo(value = T, strategy = Closure.DELEGATE_FIRST)</code> annotation is added to each
 * method parameter annotated by <code>@DslContext(T)</code>.
 */
@GroovyASTTransformation(phase = CompilePhase.CONVERSION)
class ContextASTTransformation implements ASTTransformation {
    private static final ClassNode DELEGATES_TO_CLASS = ClassHelper.make('groovy.lang.DelegatesTo')
    private static final ConstantExpression DELEGATE_FIRST_EXPRESSION = new ConstantExpression(DELEGATE_FIRST, true)

    @Override
    void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        sourceUnit.AST?.classes*.methods*.parameters.flatten().each { Parameter parameter ->
            List<AnnotationNode> dslContextAnnotations = parameter.getAnnotations(ClassHelper.make('DslContext')) +
                    parameter.getAnnotations(ClassHelper.make('javaposse.jobdsl.dsl.DslContext'))
            if (!dslContextAnnotations.empty) {
                AnnotationNode delegatesToAnnotation = new AnnotationNode(DELEGATES_TO_CLASS)
                delegatesToAnnotation.addMember('value', dslContextAnnotations[0].members.value)
                delegatesToAnnotation.addMember('strategy', DELEGATE_FIRST_EXPRESSION)
                parameter.annotations << delegatesToAnnotation
            }
        }
    }
}
