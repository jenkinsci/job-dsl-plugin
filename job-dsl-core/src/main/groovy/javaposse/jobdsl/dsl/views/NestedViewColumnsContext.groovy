package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context

class NestedViewColumnsContext implements Context {
    List<Node> columnNodes = []

    void status() {
        columnNodes << new Node(null, 'hudson.views.StatusColumn')
    }

    void weather() {
        columnNodes << new Node(null, 'hudson.views.WeatherColumn')
    }
}
