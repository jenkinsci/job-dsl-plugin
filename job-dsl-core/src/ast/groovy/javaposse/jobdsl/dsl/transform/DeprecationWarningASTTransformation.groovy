package javaposse.jobdsl.dsl.transform

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * Global AST transformation for logging deprecation warnings.
 *
 * Each method in a class implementing <code>javaposse.jobdsl.dsl.Context</code> annotated by {@link Deprecated} is
 * supplemented with a method call to <code>jobManagement.logDeprecationWarning()</code>. The method must have access to
 * a <code>jobManagement</code> field.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class DeprecationWarningASTTransformation implements ASTTransformation {
    private static final ClassNode DEPRECATED_ANNOTATION = ClassHelper.make('java.lang.Deprecated')
    private static final ClassNode CONTEXT_CLASS = ClassHelper.make('javaposse.jobdsl.dsl.Context')

    @Override
    void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        List<ClassNode> classes = sourceUnit.AST.classes.findAll {
            !it.interface && it.implementsInterface(CONTEXT_CLASS)
        }
        classes.methods.flatten().each { MethodNode method ->
            List<AnnotationNode> annotations = method.getAnnotations(DEPRECATED_ANNOTATION)
            if (annotations) {
                VariableExpression jobManagementVariable = ASTTransformationHelper.getJobManagementVariable(
                        sourceUnit,
                        method.declaringClass,
                        Token.newString(annotations[0].text, annotations[0].lineNumber, annotations[0].columnNumber)
                )

                ((BlockStatement) method.code).statements.add(0, new ExpressionStatement(new MethodCallExpression(
                        jobManagementVariable,
                        new ConstantExpression('logDeprecationWarning'),
                        new ArgumentListExpression()
                )))
            }
        }
    }
}
