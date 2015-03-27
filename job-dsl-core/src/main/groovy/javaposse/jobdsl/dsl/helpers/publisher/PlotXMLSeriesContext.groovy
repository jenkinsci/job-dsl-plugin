package javaposse.jobdsl.dsl.helpers.publisher

import static com.google.common.base.Preconditions.checkArgument

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

    void nodeType(String nodeType) {
        checkArgument(NODE_TYPES.contains(nodeType), "nodeType must be one of ${NODE_TYPES.join(', ')}")
        this.nodeType = nodeType
    }

    void url(String url) {
        this.url = url
    }

    void xpath(String xpath) {
        this.xpath = xpath
    }
}
