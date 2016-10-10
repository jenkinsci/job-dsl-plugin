App.Settings = Marionette.Object.extend({

    initialize: function() {
        var parsed = JSON.parse(localStorage.getItem('job-dsl-api-viewer')) || {};
        this.excludedPlugins = parsed.excludedPlugins || [];
    },

    save: function() {
        localStorage.setItem('job-dsl-api-viewer', JSON.stringify({
            excludedPlugins: this.excludedPlugins
        }));
        this.trigger('change');
    },

    isPluginExcluded: function(name) {
        return _.contains(this.excludedPlugins, name);
    },

    isPluginsExcluded: function(plugins) {
        return _.isEmpty(plugins)
            || _.some(plugins, _.isUndefined)
            || _.pluck(plugins, 'name').some(this.isPluginExcluded.bind(this));
    },

    setPluginExcluded: function(name, isExcluded) {
        if (isExcluded && !this.isPluginExcluded(name)) {
            this.excludedPlugins.push(name);
        }
        if (!isExcluded && this.isPluginExcluded(name)) {
            this.excludedPlugins = _.without(this.excludedPlugins, name);
        }
        this.save();
    },

    includeAllPlugins: function() {
        this.excludedPlugins = [];
        this.save();
    },

    excludeAllPlugins: function(names) {
        this.excludedPlugins = names;
        this.save();
    }
});
