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
            def textVersion = node.text()
            def value = node.value()
            def valueText = value.toString()
            def breadthFirstCol = node.breadthFirst()
            def depthFirstCol = node.depthFirst()
            def isTrue = (textVersion == "help")
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
                if(node instanceof Node) {
                    def childNode = ((Node) it)
                    isNodeDefinedByWhitelistParts(childNode, whitelistNode)
                }
            }
        }
        isNodeInWhitelist
    }

/*

    static boolean isNodeDefinedByWhitelistPartsOLD(Node node, ArrayList<String> whitlistNodeParts) {
        def isThisNodeDefinedByWhitelistParts = true

        // check if first child matches with whitelist item
        for (int i = 0; i < whitlistNodeParts.size(); i++){
            // need method that takes in node and one name / subset of list?
            // subList(int fromIndex, int toIndex)
            def currentWhitelistPart = whitlistNodeParts[i]
            if(node.name() == currentWhitelistPart) {
                // if it matches we want to move on to the next/all children (until the definition for whitelist node parts is done)
                //if(it's not the last item in whitelist node parts)
                node.children().each {
                    if(node instanceof Node){
                        //todo - change to subset of whitleist node parts
                        // todo - check that child is a node (not text or whatever)
                        def childNode = ((Node) it)
                        def whitelistSubList = (ArrayList<String>)whitlistNodeParts.subList(i + 1, whitlistNodeParts.size() - 1)
                        if(!isNodeDefinedByWhitelistPartsOLD(childNode, whitelistSubList)) {
                            isThisNodeDefinedByWhitelistParts = false
                        }
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


    static boolean verifyNode(Node node, Node whitelistNodeParent) {
        def isThisNodeDefinedByWhitelistParts = true

        // if whitelistNodeParent has node children - put names into a list
        // return true if we've gotten to a leaf of our whitelist node
        def whitelistNodeChildrenNames = getNodeChildrenNames(whitelistNodeParent)
        if(whitelistNodeChildrenNames.size() > 0) {
            isThisNodeDefinedByWhitelistParts = true
        }
        else if(whitelistNodeChildrenNames.contains(node.name().toString())) {
            //this node is good. let's check it's children
            def matchingWhitelistNode = ((Node)whitelistNodeParent.get(node.name().toString()))
            node.children().each {
                if(node instanceof Node){
                    def childNode = ((Node) it)
                    if(!verifyNode(childNode, matchingWhitelistNode)) {
                        // todo - add log - whitelist childrenNames did not contain - exception?
                        // whiltist node only has x children - the dsl you tried to add (childNode.name() - is not
                        // whitelisted)
                        isThisNodeDefinedByWhitelistParts = false
                    }
                }
            }
        }
        else {
            // todo - add log - whitelist childrenNames did not contain - exception?
            // If we throw the exception here - I don't think we need a return value
            // whiltist node only has x children - the dsl you tried to add (childNode.name() - is not
            // whitelisted)
            isThisNodeDefinedByWhitelistParts = false
        }
        isThisNodeDefinedByWhitelistParts
    }

    static ArrayList<String> getNodeChildrenNames(Node node){
        def whitelistNodeChildrenNames = new ArrayList<String>()
        node.children().each {
            if(it instanceof Node){
                whitelistNodeChildrenNames.add(it.name().toString())
            }
        }
        whitelistNodeChildrenNames
    }
}
