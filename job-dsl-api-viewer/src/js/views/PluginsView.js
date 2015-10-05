App.PluginsView = Marionette.ItemView.extend({

    template: 'plugins',

    events: {
        'click .checkbox-wrapper': 'onWrapperClick'
    },

    initialize: function(options) {
        this.settings = options.settings;
        this.pluginList = options.pluginList;
        this.listenTo(this.settings, 'change', this.onRender);
    },

    onRender: function() {
        this.$('li').each(function(index, el) {
            var $el = $(el);
            var name = $el.data('pluginName');
            var checked = !this.settings.isPluginExcluded(name);
            $el.find('input').prop('checked', checked);
        }.bind(this));
    },

    onWrapperClick: function(e) {
        e.stopPropagation();
        var $checkbox = $(e.currentTarget).find('input');
        var name = $checkbox.closest('li').data('pluginName');
        if (!$(e.target).is('input')) {
            $checkbox.prop('checked', !$checkbox.prop('checked'));
        }
        this.settings.setPluginExcluded(name, !$checkbox.prop('checked'));
    },

    serializeData: function() {
        var pluginList = this.pluginList;
        return {
            numPlugins: pluginList.length,
            plugins: pluginList
        };
    }
});
