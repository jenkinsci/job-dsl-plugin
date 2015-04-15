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
 * Global AST transformation for checking Jenkins core version.
 *
 * Each method annotated by <code>@javaposse.jobdsl.dsl.RequiresCore</code> is supplemented with a method call to
 * <code>jobManagement.requireMinimumCoreVersion()</code>. The method must have access to a <code>jobManagement</code>
 * field.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class CoreVersionASTTransformation implements ASTTransformation {
    private static final ClassNode REQUIRES_CORE_ANNOTATION = ClassHelper.make('javaposse.jobdsl.dsl.RequiresCore')

    @Override
    void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        sourceUnit.AST?.classes*.methods.flatten().each { MethodNode method ->
            method.getAnnotations(REQUIRES_CORE_ANNOTATION).each { AnnotationNode requiresCoreAnnotation ->
                if (!method.declaringClass.getField('jobManagement')) {
                    sourceUnit.errorCollector.addError(
                            "no jobManagement field in $method.declaringClass",
                            Token.newString(
                                    requiresCoreAnnotation.text,
                                    requiresCoreAnnotation.lineNumber,
                                    requiresCoreAnnotation.columnNumber
                            ),
                            sourceUnit
                    )
                }

                MethodCallExpression pluginCheckStatement = new MethodCallExpression(
                        new VariableExpression('jobManagement'),
                        new ConstantExpression('requireMinimumCoreVersion'),
                        new ArgumentListExpression(
                                requiresCoreAnnotation.members.minimumVersion,
                        )
                )

                ((BlockStatement) method.code).statements.add(0, new ExpressionStatement(pluginCheckStatement))
            }
        }
    }
}
