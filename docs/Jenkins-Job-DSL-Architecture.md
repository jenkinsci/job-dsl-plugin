# Overview
The "Jenkins Job DSL" is made up of two parts: The Domain Specific Language itself (which allows users to write Job DSL scripts in a groovy-based language); and a Jenkins Plugin which manages the Scripts and updating of the Jenkins jobs which are created and maintained as a result.

# Detail
The DSL job scripts are executed in the context of the Plugin-managed JobParent class which adds a "job" function to define individual jobs.  This function creates a Job instance that has DSL executing methods to support the job being configured. Both the JobParent and Job defer to an instance of a JobManagement class to load and save an updated Jenkins config.xml.

The DslScriptLoader will setup and execute DSL scripts within the provided JobManagement instance. The FileJobManagement class is an implementation of this which loads/saves Job configs from the local filesystem. While the Jenkins plugin is running from ExecuteDslScripts, it creates a JobManagement instance which calls back into Jenkins. The primary entry point of the plugin is ExecuteDslScripts, which is responsible for consuming the "seed" job's definition of where the DSL files are. As it finds DSL script files, it sets up a JobManagement instance and calls DslScriptLoader.

# Execution Engine

The GroovyScriptEngine is used to execute the script. It's been provided with a workspace handler, so that will look for classes in the workspace, meaning that if you create a mypackage/MyClass.groovy file, it'll use it if you do 'import mypackage.MyClass'.
