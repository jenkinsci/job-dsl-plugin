import Marionette from "backbone.marionette";

import Root from "../../hbs/root.hbs";

export default Marionette.View.extend({
    template: Root,

    regions: {
        contextRegion: ".context-methods-section",
        detailRegion: ".detail-wrapper",
        pluginRegion: ".plugins-body",
        treeRegion: ".tree-wrapper",
    },
});
