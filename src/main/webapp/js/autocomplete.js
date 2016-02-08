$(function() {

    $("#topic_title").autocomplete({
        autoFocus: true,
        delay: 0,
        minLength: 0,
        source: "/api/v1/suggest-movies-names",

        response: function(event, ui) {
            $.each(ui.content, function(key,value) {
                value.value = value.title;
                value.label = value.title;
            });
        },

        select: function(event, ui) {
            var url = ui.item.id;
            alert("Selected movie : " + ui.item.title + " #" + ui.item.id);
        },
    });

});