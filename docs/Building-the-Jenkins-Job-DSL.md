This page covers the steps required to build and package the job-dsl-plugin from source.  These are command-line instructions. If you have an IDE, go for your life. The steps should be very similar.

## Pre-Requisites
* Java 1.6 JDK (or above)

## Steps
1. Get the source code by cloning the project's git repo
2. Open a command line window and cd to the jenkins-job-dsl directory that you cloned to
3. Run "./gradlew server". This will first download the appropriate version of Gradle. Then it compile and package the plugin .jpi and also start a local Jenkins server with the plugin installed
4. Open a browser and navigate to http://localhost:8080 to see the Jenkins UI

## IDEs
IntelliJ and Eclipse (STS) has the ability to open Gradle projects directly, but they both have their own issues actually running Gradle and running tests. So below are some instructions until Gradle/IDE support gets better.

### Creating an IDEA project
1. ./gradlew clean cleanIdea
2. ./gradlew idea
3. In IntelliJ, "Open Project" and open the .ipr file
4. Open the "Ant Build" side window and add job-dsl-plugin/build.xml
5. Run the "clean" and "test" targets

### Creating an Eclipse project
Type: ./gradlew cleanEclipse eclipse
