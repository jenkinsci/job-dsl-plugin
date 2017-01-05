package javaposse.jobdsl.dsl

/**
 * Helper class for checking if nodes proceesed are whitelisted
 */
class WhitelistContextHelper {

    private WhitelistContextHelper() {
    }

/*
    static List<String> getConfigureBlockParentNodeNames(Closure configureBlock) {
        Node node = new Node(null, 'parent')
        List<String> parentNodeNames = new LinkedList<String>()
        if (configureBlock) {
            Closure dup = ((Closure)configureBlock.clone())
            dup.delegate = new MissingPropertyToStringDelegate(node)

            use(NodeEnhancement) {
                dup.call(node)
            }
            node.children().each {
                //todo - make sure we are ok if name property doesn't exist or is null
                parentNodeNames.add(it.name())
            }
        }

        parentNodeNames
    }
*/

    static boolean verifyConfigureBlock(Closure configureBlock, Node whitelistNode) {
        boolean isNodeInWhitelist = false
        Node node = new Node(null, 'parent')

        if (configureBlock) {
            // convert configure block to node form
            Closure dup = ((Closure)configureBlock.clone())
            dup.delegate = new MissingPropertyToStringDelegate(node)

            use(NodeEnhancement) {
                dup.call(node)
            }

            // check that configure block node exists in whitelist
            node.children().each {
                if (it instanceof Node) {
                    Node childNode = ((Node) it)
                    verifyNode(childNode, whitelistNode)
                }
            }
        }
        isNodeInWhitelist
    }
/*
    static boolean isNodeDefinedByWhitelistPartsOLD(Node node, ArrayList<String> whitlistNodeParts) {
        boolean isThisNodeDefinedByWhitelistParts = true

        // check if first child matches with whitelist item
        for (int i = 0; i < whitlistNodeParts.size(); i++){
            // need method that takes in node and one name / subset of list?
            // subList(int fromIndex, int toIndex)
            if(node.name() == currentWhitelistPart) {
                // if it matches we want to move on to the next/all children (until the definition for whitelist node
                 p arts is done)
                //if(it's not the last item in whitelist node parts)
                node.children().each {
                    if (node instanceof Node) {

                    }
                }
            }
            else {
                return false
            }
        }
        isThisNodeDefinedByWhitelistParts
    }
*/
    static boolean verifyNode(Node nodeToVerify, Node whitelistNodeParent) {
        boolean isThisNodeDefinedByWhitelistParts = true

        // get Whitelist Node Parent Children names
        List<String> whitelistNodeChildrenNames = getNodeChildrenNames(whitelistNodeParent)
        if (whitelistNodeChildrenNames.size() <= 0) {
            // if no whitelist children, nodeToVerify is valid
            isThisNodeDefinedByWhitelistParts = true
        }
        else if (whitelistNodeChildrenNames.contains(nodeToVerify.name().toString())) {
            //this nodeToVerify is good. let's check it's children
            NodeList matchingWhitelistNodeList = ((NodeList)whitelistNodeParent[nodeToVerify.name().toString()])
            // since we assume that we will only get one matching nodeToVerify form the whitelist nodes (convention of
            // writing your whitelist xml), we will always only check the first nodeToVerify that matches
            Node matchingWhitelistNode = ((Node)matchingWhitelistNodeList.get(0))
            nodeToVerify.children().each {
                if (it instanceof Node) {
                    Node childNode = ((Node) it)
                    if (!verifyNode(childNode, matchingWhitelistNode)) {
                        // todo - add log - whitelist childrenNames did not contain - exception?
                        // whiltist nodeToVerify only has x children - the dsl you tried to add (childNode.name() - is not
                        // whitelisted)
                        isThisNodeDefinedByWhitelistParts = false
                    }
                }
            }
        }
        else {
            // no whitelisted children matched our node to verify. Therefore the node is not valid
            // todo - add log - whitelist childrenNames did not contain - exception?
            // If we throw the exception here - I don't think we need a return value
            // whiltist node only has x children - the dsl you tried to add (childNode.name() - is not
            // whitelisted)
            isThisNodeDefinedByWhitelistParts = false
        }
        isThisNodeDefinedByWhitelistParts
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
