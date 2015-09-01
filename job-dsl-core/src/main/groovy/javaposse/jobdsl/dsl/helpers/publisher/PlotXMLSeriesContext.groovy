package javaposse.jobdsl.dsl.helpers.publisher

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class PlotXMLSeriesContext extends PlotSeriesContext {
    private static final List<String> NODE_TYPES = [
        'NODESET', 'NODE', 'STRING', 'BOOLEAN', 'NUMBER'
    ]

    String nodeType = 'NODESET'
    String url
    String xpath

    PlotXMLSeriesContext(String fileName) {
        super(fileName, 'xml', 'XMLSeries')
    }

    /**
     * Sets the XPath result type. Must be one of {@code 'NODESET'} (default), {@code 'NODE'}, {@code 'STRING'},
     * {@code 'BOOLEAN'} or {@code 'NUMBER'}.
     */
    void nodeType(String nodeType) {
        checkArgument(NODE_TYPES.contains(nodeType), "nodeType must be one of ${NODE_TYPES.join(', ')}")
        this.nodeType = nodeType
    }

    /**
     * If set, opens the URL when a point is clicked.
     */
    void url(String url) {
        this.url = url
    }

    /**
     * Specifies an XPath which selects the values that should be plotted.
     */
    void xpath(String xpath) {
        this.xpath = xpath
    }
}
