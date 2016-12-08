package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.ScmContext
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext

abstract class Item extends AbstractContext {
    String name
    String[] jobDslWhitelist

    private final List<Closure> configureBlocks = []

    protected Item(JobManagement jobManagement, String name) {
        super(jobManagement)
        this.name = name
        this.jobDslWhitelist = (jobManagement) ? jobManagement.getJobDslWhitelist() : new String[0]
    }

    @Deprecated
    protected Item(JobManagement jobManagement) {
        super(jobManagement)
        this.jobDslWhitelist = (jobManagement) ? jobManagement.getJobDslWhitelist() : new String[0]
    }

    @Deprecated
    void setName(String name) {
        this.name = name
}

    /**
     * Allows direct manipulation of the generated XML.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        if(jobManagement.executeOnlyWhitelistedDsl()) {
            // whitelisting is turned on
            if (isClosureFromExternalClass(configureBlock)) {
                // closure is loaded from an external class
                String closureParentClass = configureBlock.thisObject['name'];
                checkExternalClassIsWhitelisted(closureParentClass, jobDslWhitelist)
            } else {
                // we do not allow raw configure blocks that do are not loaded from a whitelisted external class when
                // whitelisting is turned on
                throw new DslScriptException("The job dsl block at the current line is not added to the whitelist.\n" +
                        "If this is a raw configure block, you can avoid this error, by either pull this block into an " +
                        "external class and whitelist that class, or turn whitelisting off.\n" +
                        "If this is not a raw Configure block, this job dsl is not eligable to be whitelisted, so your" +
                        "only option is to not use this job dsl block type, or turn whitelisting off.");
            }
        }
        configureBlocks << configureBlock
    }

    /**
     * Configure block called by steps, publishers, scm, or wrapper job methods. These are only job part types
     * that have the option to be whitelisted (besides base configure blocks pulled into external classes)
     *
     * Checks if job part is in whitelist if whitelisting is turned on.
     */
    void configure(Closure configureBlock, Closure originalClosure, Context originalContext) {
        if(jobManagement.executeOnlyWhitelistedDsl()) {
            // whitelisting is turned on
            checkConfigureBlockOrContextIsValid(originalClosure, originalContext, jobDslWhitelist)
        }
        configureBlocks << configureBlock
    }

    /**
     * Postpone all xml processing until someone actually asks for the xml. That lets us execute everything in order,
     * even if the user didn't specify them in order.
     */
    String getXml() {
        Writer xmlOutput = new StringWriter()
        XmlNodePrinter xmlNodePrinter = new XmlNodePrinter(new PrintWriter(xmlOutput), '    ')
        xmlNodePrinter.with {
            preserveWhitespace = true
            expandEmptyElements = true
            quote = "'" // Use single quote for attributes
        }
        xmlNodePrinter.print(node)

        xmlOutput.toString()
    }

    Map getProperties() {
        // see JENKINS-22708
        throw new UnsupportedOperationException()
    }

    Node getNode() {
        Node node = nodeTemplate
        ContextHelper.executeConfigureBlocks(node, configureBlocks)
        node
    }

    protected Node getNodeTemplate() {
        new XmlParser().parse(this.class.getResourceAsStream("${this.class.simpleName}-template.xml"))
    }

    @Deprecated
    void executeWithXmlActions(Node root) {
        ContextHelper.executeConfigureBlocks(root, configureBlocks)
    }

    /**
     * Checks if external class is whitelisted
     *
     * Throws error with proper messaging if it is not
     */
    private static void checkExternalClassIsWhitelisted(String externalClassName, String[] jobDslWhitelist) {
            // This closure was inherited from an external class. Let's check if it's on our whitelist
            if (!jobDslWhitelist.contains(externalClassName)) {
                throw new DslScriptException(String.format("The parent class for the job dsl on this line - %s - is not " +
                        "added to the whitelist. To avoid this error, " +
                        "either add this class to the whitelist, or turn whitelisting off", externalClassName));
            }
    }

    /**
     * Checks if external class is whitelisted
     *
     * Throws error with proper messaging if it is not
     */
    private static void checkJobDslNodeIsWhitelisted(String nodeClassName, String[] jobDslWhitelist) {
        // This closure was inherited from an external class. Let's check if it's on our whitelist
        if (!jobDslWhitelist.contains(nodeClassName)) {
            throw new DslScriptException(String.format("The parent class for the job dsl on this line - %s - is not " +
                    "added to the whitelist. To avoid this error, " +
                    "either add this class to the whitelist, or turn whitelisting off", nodeClassName));
        }
    }

    private static boolean isClosureFromExternalClass(Closure closure){
        if(closure.thisObject.hasProperty('name') && closure.thisObject['name']) {
            return true
        }
        return false
    }

        private static void checkConfigureBlockOrContextIsValid(Closure closure, Context context, String[] jobDslWhitelist) {
            if(isClosureFromExternalClass(closure)) {
                // if closure is loaded from an external class
                String closureParentClass = closure.thisObject['name'];
                checkExternalClassIsWhitelisted(closureParentClass, jobDslWhitelist)
            }
            else {
                // check which Context it inherits from
                if(context instanceof PublisherContext) {
                    // if we didn't inherit this closure from an external class - we check what type of StepNodes we have
                    ((PublisherContext)context).publisherNodes.each {
                        if(it.name() != null) {
                            checkJobDslNodeIsWhitelisted((String)it.name(), jobDslWhitelist)
                        } else {
                            // todo - should we do something if the it.name() is null??
                        }
                    }
                } else if(context instanceof WrapperContext) {
                    // if we didn't inherit this closure from an external class - we check what type of StepNodes we have
                    ((WrapperContext)context).wrapperNodes.each {
                        if(it.name() != null) {
                            checkJobDslNodeIsWhitelisted((String)it.name(), jobDslWhitelist)
                        } else {
                            // todo - should we do something if the it.name() is null??
                        }
                    }
                } else if(context instanceof StepContext) {
                    ((StepContext)context).stepNodes.each {
                        if(it.name() != null) {
                            checkJobDslNodeIsWhitelisted((String)it.name(), jobDslWhitelist)
                        } else {
                            // todo - should we do something if the it.name() is null??
                        }
                    }
                    // if we've gotten to this point and not thrown an exception, all of the step nodes were
                    // apart of our whitelist
                } else if(context instanceof ScmContext) {
                    // if we didn't inherit this closure from an external class - we check what type of StepNodes we have
                    ((ScmContext)context).scmNodes.each {
                        if(it.name() != null) {
                            checkJobDslNodeIsWhitelisted((String)it.name(), jobDslWhitelist)
                        } else {
                            // todo - should we do something if the it.name() is null??
                        }
                    }
                }
                else if(context instanceof TriggerContext) {
                    // if we didn't inherit this closure from an external class - we check what type of StepNodes we have
                    ((TriggerContext)context).triggerNodes.each {
                        if(it.name() != null) {
                            checkJobDslNodeIsWhitelisted((String)it.name(), jobDslWhitelist)
                        } else {
                            // todo - should we do something if the it.name() is null??
                        }
                    }
                }
                else {
                    throw new DslScriptException(String.format("This context - %s - is not eligable to be whitelisted. To avoid this error, " +
                            "do not use jobdsl from this context type or turn whitelisting off", context.toString()));
                }
            }
        }
}
