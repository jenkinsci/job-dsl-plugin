App.ContextView = Marionette.ItemView.extend({

    template: 'context',

    serializeData: function() {
        return {
            signatures: this.options.signatures
        };
    }
});
