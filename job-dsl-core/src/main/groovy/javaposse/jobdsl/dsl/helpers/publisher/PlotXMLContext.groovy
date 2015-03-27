package javaposse.jobdsl.dsl.helpers.publisher

import static com.google.common.base.Preconditions.checkArgument

class PlotXMLContext extends PlotSeriesContext {
    private static final List<String> NODETYPES = [
        'NODESET', 'NODE', 'STRING', 'BOOLEAN', 'NUMBER'
    ]

    String nodeType = 'NODESET'
    String url
    String xpath

    PlotXMLContext(String fileName) {
        super(fileName, 'xml', 'XMLSeries')
    }

    void nodeType(String nodeType) {
        checkArgument(NODETYPES.contains(nodeType), "nodeType must be one of ${NODETYPES.join(', ')}")
        this.nodeType = nodeType
    }

    void url(String url) {
        this.url = url
    }

    void xpath(String xpath) {
        this.xpath = xpath
    }
}
