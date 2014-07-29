package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.helpers.Context

class LockableResourcesContext implements Context {
    def resourceNames = null
    def resourceNamesVar = null
    def resourceNumber = -1

	def resourceNames(String resourceNames) {
        this.resourceNames = resourceNames
	}
	def resourceNamesVar(String resourceNamesVar) {
        this.resourceNamesVar = resourceNamesVar
	}
	def resourceNumber(int resourceNumber) {
        this.resourceNumber = resourceNumber
	}
}
