function addAutoComplete(input,callbackOnSelect) {
    input.autocomplete({
        autoFocus: true,
        delay: 0,
        minLength: 0,
        source: "/api/v1/find-movies",

        response: function(event, ui) {
            $.each(ui.content, function(key,value) {
                value.value = value.title;
                value.label = value.title;
            });
        },

        select: function(event, ui) {
            callbackOnSelect(ui.item);
        },
    });
}