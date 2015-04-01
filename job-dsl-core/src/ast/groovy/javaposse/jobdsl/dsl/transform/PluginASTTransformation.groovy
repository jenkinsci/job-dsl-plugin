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
 * Global AST transformation for checking plugin requirements.
 *
 * Each method annotated by <code>@javaposse.jobdsl.dsl.RequiresPlugin</code> is supplemented with a method call to
 * <code>jobManagement.requireMinimumPluginVersion()</code> or <code>jobManagement.requirePlugin()</code>. The method
 * must have access to a <code>jobManagement</code> field.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class PluginASTTransformation implements ASTTransformation {
    private static final ClassNode REQUIRES_PLUGIN_ANNOTATION = ClassHelper.make('javaposse.jobdsl.dsl.RequiresPlugin')

    @Override
    void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        sourceUnit.AST?.classes*.methods.flatten().each { MethodNode method ->
            method.getAnnotations(REQUIRES_PLUGIN_ANNOTATION).each { AnnotationNode requiresPluginAnnotation ->
                if (!method.declaringClass.getField('jobManagement')) {
                    sourceUnit.errorCollector.addError(
                            "no jobManagement field in $method.declaringClass",
                            Token.newString(
                                    requiresPluginAnnotation.text,
                                    requiresPluginAnnotation.lineNumber,
                                    requiresPluginAnnotation.columnNumber
                            ),
                            sourceUnit
                    )
                }

                MethodCallExpression pluginCheckStatement
                if (requiresPluginAnnotation.members.minimumVersion) {
                    pluginCheckStatement = new MethodCallExpression(
                            new VariableExpression('jobManagement'),
                            new ConstantExpression('requireMinimumPluginVersion'),
                            new ArgumentListExpression(
                                    requiresPluginAnnotation.members.id,
                                    requiresPluginAnnotation.members.minimumVersion,
                            )
                    )
                } else {
                    pluginCheckStatement = new MethodCallExpression(
                            new VariableExpression('jobManagement'),
                            new ConstantExpression('requirePlugin'),
                            new ArgumentListExpression(
                                    requiresPluginAnnotation.members.id,
                            )
                    )
                }

                ((BlockStatement) method.code).statements.add(0, new ExpressionStatement(pluginCheckStatement))
            }
        }
    }
}
