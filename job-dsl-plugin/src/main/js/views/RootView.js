import { View } from 'backbone.marionette';

import Root from '../../hbs/root.hbs';

export default View.extend({
    template: Root,

    regions: {
        contextRegion: ".context-methods-section",
        detailRegion: ".detail-wrapper",
        pluginRegion: ".plugins-body",
        treeRegion: ".tree-wrapper",
    },
});
