package javaposse.jobdsl.dsl.transform

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
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
        List<ClassNode> classes = sourceUnit.AST.classes.findAll { !it.interface }
        classes.methods.flatten().each { MethodNode method ->
            List<AnnotationNode> annotations = method.getAnnotations(REQUIRES_PLUGIN_ANNOTATION)
            method.declaringClass.allInterfaces.each { ClassNode iface ->
                MethodNode interfaceMethod = iface.getMethod(method.name, method.parameters)
                if (interfaceMethod) {
                    annotations.addAll(interfaceMethod.getAnnotations(REQUIRES_PLUGIN_ANNOTATION))
                }
            }

            annotations.each { AnnotationNode annotationNode ->
                VariableExpression jobManagementVariable = ASTTransformationHelper.getJobManagementVariable(
                        sourceUnit,
                        method.declaringClass,
                        Token.newString(annotationNode.text, annotationNode.lineNumber, annotationNode.columnNumber)
                )

                ArgumentListExpression argumentList = new ArgumentListExpression(annotationNode.members.id)
                Expression methodExpression
                if (annotationNode.members.minimumVersion) {
                    argumentList.addExpression(annotationNode.members.minimumVersion)
                    methodExpression = new ConstantExpression('requireMinimumPluginVersion')
                } else {
                    methodExpression = new ConstantExpression('requirePlugin')
                }
                if (annotationNode.members.failIfMissing) {
                    argumentList.addExpression(annotationNode.members.failIfMissing)
                }

                MethodCallExpression pluginCheckStatement = new MethodCallExpression(
                        jobManagementVariable,
                        methodExpression,
                        argumentList
                )

                ((BlockStatement) method.code).statements.add(0, new ExpressionStatement(pluginCheckStatement))
            }
        }
    }
}
