import Marionette from "backbone.marionette";

import PluginDetail from "../../hbs/pluginDetail.hbs";

export default Marionette.View.extend({
    template: PluginDetail,

    serializeData: function() {
        return {
            plugin: this.options.plugin,
            usages: this.options.usages
        };
    }
});
