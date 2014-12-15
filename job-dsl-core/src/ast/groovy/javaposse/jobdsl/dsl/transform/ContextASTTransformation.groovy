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

@GroovyASTTransformation(phase = CompilePhase.CONVERSION)
class ContextASTTransformation implements ASTTransformation {
    private static final ClassNode DELEGATES_TO_CLASS = ClassHelper.make('groovy.lang.DelegatesTo')
    private static final ConstantExpression DELEGATE_FIRST_EXPRESSION = new ConstantExpression(DELEGATE_FIRST, true)

    @Override
    void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        sourceUnit.AST?.classes*.methods*.parameters.flatten().each { Parameter parameter ->
            List<AnnotationNode> dslContextAnnotations = parameter.getAnnotations(ClassHelper.make('DslContext'))
            if (!dslContextAnnotations.empty) {
                AnnotationNode delegatesToAnnotation = new AnnotationNode(DELEGATES_TO_CLASS)
                delegatesToAnnotation.members.value = dslContextAnnotations[0].members.value
                delegatesToAnnotation.members.strategy = DELEGATE_FIRST_EXPRESSION
                parameter.annotations << delegatesToAnnotation
            }
        }
    }
}
