package javaposse.jobdsl.dsl

import java.util.logging.Logger
import java.util.logging.Level

/**
 * Helper class for checking if nodes proceesed are valid according the user specified Whitelist
 */
class WhitelistHelper {
    private static final Logger LOGGER = Logger.getLogger(WhitelistHelper.name)

    private WhitelistHelper() {
    }

    static void verifyExternalClassThatDefinesConfigureBlock(Closure configureBlock,
                                                                     String[] allowedExternalClasses) {
        if (isClosureFromExternalClass(configureBlock)) {
            // if this closure is defined in an external class, let's check it
            String closureParentClass = configureBlock.delegate['name']
            verifyExternalClass(closureParentClass, allowedExternalClasses)
        }
        // if it's not we're good!
    }

    private static boolean isClosureFromExternalClass(Closure closure) {
        closure.delegate instanceof Class
    }

    /**
     * Verify that the external class that defines a job dsl block is on the list of allowed
     * external classes defining job dsl
     */
    private static void verifyExternalClass(String externalClassName, String[] externalClassWhitelist) {
        if (!externalClassWhitelist.contains(externalClassName)) {
            throw new DslScriptException(String.format('The parent class for the job dsl on this line - %s - is ' +
                    'not added to the whitelist. To avoid this error, ' +
                    'either add this class to the whitelist, or turn whitelisting off', externalClassName))
        }
    }

    /**
     * Verify that configure block is valid according to the whitelist node
     */
    static void verifyRawJobDsl(Closure configureBlock, Node whitelistNode) {
        if (!isClosureFromExternalClass(configureBlock)) {
            // if this closure is not from an external class - we have to check the raw job dsl
            Node node = new Node(null, 'project')

            if (configureBlock) {
                // convert configure block to node form - with parent as root node
                Closure dup = ((Closure) configureBlock.clone())
                dup.delegate = new MissingPropertyToStringDelegate(node)

                use(NodeEnhancement) {
                    dup.call(node)
                }

                // check that all children for the node representing this configure block are valid
                // we don't need to check root since that's simply 'project'
                verifyNodeChildren(node, whitelistNode)
            }
        }
        // if it's from an external class... we're good!
    }

    /**
     * Verify that all children of a node do not violate the whitelist by passing in two nodes; the node
     * whose children need to be verified, and the node representing the whitelist at that same level
     */
    static void verifyNodeChildren(Node nodeToVerify, Node whitelistNode) {
        // we need to check that each nodeToVerify child has a matching element in the whitelist node
        // at the correct level
        nodeToVerify.children().each {
            if (it instanceof Node) {
                Node childNode = ((Node) it)
                verifyNode(childNode, whitelistNode)
            }
        }
    }

    /**
     * Verify that a node does not violate the whitelist by passing in two nodes; the node that
     * needs to be verified, and the node representing the whitelist at one level higher that the nodeToVerify
     */
    static void verifyNode(Node nodeToVerify, Node whitelistNodeParent) {
        // get whitelist node children names
        String nodeToVerifyName = nodeToVerify.name()
        List<String> whitelistNodeChildrenNames = getNodeChildrenNames(whitelistNodeParent)
        if (whitelistNodeChildrenNames.size() <= 0) {
            // if no whitelist children, nodeToVerify is valid
            LOGGER.log(Level.FINE, String.format('No children for whitelist node - ' +
                    "${whitelistNodeParent.name()} - so current jobdsl node - ${nodeToVerifyName} - " +
                    'and all children are valid, since all parents of current jobdsl node were in whitelist'))
        }
        // if whitelist node has children, we check if nodeToVerify is valid
        else if (whitelistNodeChildrenNames.contains(nodeToVerifyName)) {
            // since we assume no duplicate names for sibling children in the whitelist node tree (convention of
            // writing your whitelist xml), we will always take the first one that matches
            NodeList matchingWhitelistNodeList = ((NodeList)whitelistNodeParent[nodeToVerifyName])
            Node matchingWhitelistNode = ((Node)matchingWhitelistNodeList.get(0))

            // recursively call same function on children nodes of nodeToVerify
            verifyNodeChildren(nodeToVerify, matchingWhitelistNode)
        }
        else {
            throw new DslScriptException(String.format("Your DSL element ${nodeToVerify.name().toString()} is not " +
                    'listed as a whitelisted element. Whitelisted elements at this ' +
                    "level include - ${whitelistNodeChildrenNames.toString()}"))
        }
    }

    static List<String> getNodeChildrenNames(Node node) {
        List<String> whitelistNodeChildrenNames = []
        node.children().each {
            if (it instanceof Node) {
                whitelistNodeChildrenNames.add(it.name().toString())
            }
        }
        whitelistNodeChildrenNames
    }
}
