import { View } from 'backbone.marionette';

import Context from '../../hbs/context.hbs';

export default View.extend({
    className: 'context-view',

    template: Context,

    serializeData: function() {
        return {
            signatures: this.options.signatures
        };
    }
});
