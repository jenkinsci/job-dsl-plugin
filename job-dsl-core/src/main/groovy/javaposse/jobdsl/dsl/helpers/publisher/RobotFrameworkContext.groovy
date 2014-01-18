package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical;

import com.google.common.base.Preconditions;

import javaposse.jobdsl.dsl.helpers.Context

/**
 * A Job DSL context for Robot Framework. Specifically, 
 * take a look at 
 * https://github.com/jenkinsci/robot-plugin/blob/master/src/main/java/hudson/plugins/robot/RobotPublisher.java
 * 
 * @author Behrooz Nobakht
 */
class RobotFrameworkContext implements Context {

	static String DEFAULT_OUTPUT_PATH = "target/robotframework-reports"
	static String DEFAULT_REPORT_FILE_NAME = "report.html"
	static String DEFAULT_OUTPUT_FILE_NAME = "output.xml"
	static String DEFAULT_LOG_FILE_NAME = "log.html"

	Double passThreshold = 100.0
	Double unstableThreshold = 0.0
	Boolean onlyCritical = false
	String outputPath = DEFAULT_OUTPUT_PATH
	String reportFileName = DEFAULT_REPORT_FILE_NAME
	String logFileName = DEFAULT_LOG_FILE_NAME
	String outputFileName = DEFAULT_OUTPUT_FILE_NAME
	
	void passThreshold(Double passThreshold) {
		this.passThreshold = passThreshold ?: this.passThreshold
	}
	
	void unstableThreshold(Double unstableThreshold) {
		this.unstableThreshold = unstableThreshold ?: this.unstableThreshold
	}
	
	void onlyCritical(Boolean onlyCritical) {
		this.onlyCritical = onlyCritical ?: this.onlyCritical
	}
	
	void outputPath(String outputPath) {
		this.outputPath = outputPath ?: this.outputPath
	}
	
	void reportFileName(String reportFileName) {
		this.reportFileName = reportFileName ?: this.reportFileName
	}
	
	void logFileName(String logFileName) {
		this.logFileName = logFileName ?: this.logFileName
	}
	
	void outputFileName(String outputFileName) {
		this.outputFileName = outputFileName ?: this.outputFileName
	}
}
