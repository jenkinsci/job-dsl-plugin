App.ContextView = Marionette.ItemView.extend({

    className: 'context-view',

    template: 'context',

    serializeData: function() {
        return {
            signatures: this.options.signatures
        };
    }
});
