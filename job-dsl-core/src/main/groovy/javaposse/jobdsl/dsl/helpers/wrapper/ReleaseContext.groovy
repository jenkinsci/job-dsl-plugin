package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.step.AbstractStepContext

class ReleaseContext implements Context { 
  	String releaseVersionTemplate
  	Boolean doNotKeepLog
  	Boolean overrideBuildParameters
  	List<Node> preBuildSteps = []
  	List<Node> postSuccessfulBuildSteps = []
  	List<Node> postBuildSteps = []
  	List<Node> postFailedBuildSteps = []
  	Closure configureBlock

  	def preBuildSteps(Closure closure) { 
    	def stepContext = new AbstractStepContext()
    	AbstractContextHelper.executeInContext(closure, stepContext)
    	preBuildSteps << stepContext.stepNodes
  	}

  	def postSuccessfulBuildSteps(Closure closure) { 
    	def stepContext = new AbstractStepContext()
    	AbstractContextHelper.executeInContext(closure, stepContext)
    	postSuccessfulBuildSteps << stepContext.stepNodes
  	}

  	def postBuildSteps(Closure closure) { 
    	def stepContext = new AbstractStepContext()
    	AbstractContextHelper.executeInContext(closure, stepContext)
    	postBuildSteps << stepContext.stepNodes
  	}

  	def postFailedBuildSteps(Closure closure) { 
    	def stepContext = new AbstractStepContext()
    	AbstractContextHelper.executeInContext(closure, stepContext)
    	postFailedBuildSteps << stepContext.stepNodes
  	}

  	def releaseVersionTemplate(String releaseVersionTemplate) { 
    	this.releaseVersionTemplate = releaseVersionTemplate
  	}

  	def doNotKeepLog(Boolean doNotKeepLog) { 
    	this.doNotKeepLog = doNotKeepLog
  	}

  	def overrideBuildParameters(Boolean overrideBuildParameters) { 
    	this.overrideBuildParameters = overrideBuildParameters
  	}
    
    def configure(Closure closure) {
        this.configureBlock = closure
    }
    
}