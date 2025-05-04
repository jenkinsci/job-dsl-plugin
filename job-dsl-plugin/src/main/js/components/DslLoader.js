import { MnObject } from 'backbone.marionette';
import Dsl from './Dsl.js';

/**
 * Loads and caches DSL data.
 */
export default MnObject.extend({
    initialize: function() {
        this.dslsByUrl = {};
    },

    fetch: function(url) {
        var dsl = this.dslsByUrl[url];
        if (!dsl) {
            return $.get(url).then(function(data) {
                var dsl = new Dsl({ data: data });
                this.dslsByUrl[url] = dsl;
                return dsl;
            }.bind(this));
        }
        return $.Deferred().resolveWith(null, [dsl]);
    }
});
