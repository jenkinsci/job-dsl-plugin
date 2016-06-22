package javaposse.jobdsl.dsl.coercion.matrixproject

import javaposse.jobdsl.dsl.coercion.AbstractCoercer

/**
 * convert a matrix-project to a project
 */
class Project extends AbstractCoercer {

    @Override
    Node coerce(Node existing) {

        existing.name = 'project'
        existing.attributes().remove('plugin')

        existing.findAll { node ->
            node.name() =~ /(axes|executionStrategy|childCustomWorkspace|combinationFilter)/ }.each {
                existing.remove(it)
        }

        existing
    }

}
