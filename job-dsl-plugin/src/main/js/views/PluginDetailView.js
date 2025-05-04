import { View } from 'backbone.marionette';

import PluginDetail from '../../hbs/pluginDetail.hbs';

export default View.extend({
    template: PluginDetail,

    serializeData: function() {
        return {
            plugin: this.options.plugin,
            usages: this.options.usages
        };
    }
});
