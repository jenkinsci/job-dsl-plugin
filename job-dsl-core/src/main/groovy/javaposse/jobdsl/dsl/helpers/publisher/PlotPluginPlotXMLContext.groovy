package javaposse.jobdsl.dsl.helpers.publisher

class PlotPluginPlotXMLContext extends PlotPluginPlotSeriesContext {
    String url = ''
    String xpath = ''
    NodeType nodeType = NodeType.NODESET

    PlotPluginPlotXMLContext() {
        super('XMLSeries', 'xml')
    }

    void url(String url) {
        this.url = url
    }

    void xpath(String xpath) {
        this.xpath = xpath
    }

    void nodeType(NodeType nodeType) {
        this.nodeType = nodeType
    }

    static enum NodeType {
        NODESET, NODE, STRING, BOOLEAN, NUMBER
    }
}
