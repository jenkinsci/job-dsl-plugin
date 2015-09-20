App.PluginDetailView = Marionette.ItemView.extend({

    template: 'pluginDetail',

    serializeData: function() {
        return {
            plugin: this.options.plugin,
            usages: this.options.usages
        };
    }
});
