package javaposse.jobdsl.dsl.coercion.project

import javaposse.jobdsl.dsl.coercion.AbstractCoercer

/**
 * convert a project to a matrixproject
 */
class MatrixProject extends AbstractCoercer {

    @Override
    @SuppressWarnings('UnusedObject')
    Node coerce(Node existing) {
        //Node coerced = existing.clone()

        existing.name = 'matrix-project'
        existing.attributes().remove('plugin')

        new Node(existing, 'axes')

        Node es = new Node(existing, 'executionStrategy', [class: 'hudson.matrix.DefaultMatrixExecutionStrategyImpl'])
        Node rs = new Node(es, 'runSequentially' )
        rs.setValue('false')

        existing
    }

}
