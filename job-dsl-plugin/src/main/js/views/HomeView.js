import { View } from 'backbone.marionette';
import _ from 'underscore';

import ContextView from './ContextView.js';
import Home from '../../hbs/home.hbs';

export default View.extend({
    template: Home,

    regions: {
        contextRegion: '.context-methods-section'
    },

    initialize: function(options) {
        this.application = options.application;
        this.config = options.config;
        this.settings = options.settings;
        this.dsl = options.dsl;
        this.plugins = options.plugins;
        this.listenTo(this.settings, 'change', this.render);
    },

    onRender: function() {
        var pathInfo = this.dsl.getPathInfo();
        var methodNode = pathInfo.methodNode;
        var signatures = this.dsl.getContextSignatures(methodNode.contextClass);
        signatures = _.filter(signatures, function(sig) {
            return !this.settings.isPluginsExcluded(sig.plugins);
        }, this);
        var contextView = new ContextView({ signatures: signatures });
        this.showChildView("contextRegion", contextView);
    },

    onAttach: function() {
        this.application.highlightCode($(".highlight"));
        $(".detail-wrapper")
                .find(".expand-closure")
                .click(this.application.onExpandClick.bind(this.application));
    },

    serializeData: function() {
        return {plugins: this.plugins, config: this.config};
    }
});
