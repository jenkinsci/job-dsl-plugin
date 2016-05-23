package javaposse.jobdsl.dsl.transform

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.CSTNode

class ASTTransformationHelper {
    static VariableExpression getJobManagementVariable(SourceUnit sourceUnit, ClassNode clazz, CSTNode context) {
        if (!clazz.getField('jobManagement') && !clazz.getField('jm')) {
            sourceUnit.errorCollector.addError("no jobManagement field in $clazz", context, sourceUnit)
        }
        new VariableExpression(clazz.getField('jobManagement') ? 'jobManagement' : 'jm')
    }
}
