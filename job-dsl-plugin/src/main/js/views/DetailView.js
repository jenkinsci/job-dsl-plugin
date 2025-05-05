import { View } from 'backbone.marionette';
import _ from 'underscore';

import Detail from '../../hbs/detail.hbs';

export default View.extend({
    template: Detail,

    initialize: function(options) {
        this.application = options.application;
        this.dsl = options.dsl;
        this.path = options.path;
        this.settings = options.settings;
        this.listenTo(this.settings, 'change', this.render);
    },

    onAttach: function() {
        this.application.highlightCode($(".highlight"));
        $(".detail-wrapper")
                .find(".expand-closure")
                .click(this.application.onExpandClick.bind(this.application));
    },

    serializeData: function() {
        var pathInfo = this.dsl.getPathInfo(this.path);
        var methodNode = pathInfo.methodNode;
        var ancestors = pathInfo.ancestors;
        var usages = pathInfo.usages;

        var data = {
            methodNode: methodNode,
            name: methodNode.name,
            ancestors: ancestors
        };

        if (methodNode.signatures) {
            data.signatures = this.dsl.getSignatures(methodNode, this.path);
        }

        data.usages = _.sortBy(usages, function(usage) { return (usage.method.name + usage.simpleClassName).toLowerCase(); });

        return data;
    }

});
